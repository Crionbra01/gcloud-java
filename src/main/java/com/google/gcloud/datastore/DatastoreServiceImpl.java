package com.google.gcloud.datastore;

import com.google.api.services.datastore.DatastoreV1;
import com.google.api.services.datastore.client.Datastore;
import com.google.api.services.datastore.client.DatastoreException;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.gcloud.ExceptionHandler;
import com.google.gcloud.RetryHelper;
import com.google.gcloud.RetryHelper.RetryHelperException;
import com.google.gcloud.RetryParams;
import com.google.protobuf.ByteString;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;


final class DatastoreServiceImpl implements DatastoreService {

  private static final ExceptionHandler.Interceptor EXCEPTION_HANDLER_INTERCEPTOR =
      new ExceptionHandler.Interceptor() {

        private static final long serialVersionUID = 6911242958397733203L;

        @Override
        public RetryResult afterEval(Exception exception, RetryResult retryResult) {
          return null;
        }

        @Override
        public RetryResult beforeEval(Exception exception) {
          if (exception instanceof DatastoreServiceException) {
            boolean isRetriable = ((DatastoreServiceException) exception).code().isRetriable();
            return isRetriable
                ? ExceptionHandler.Interceptor.RetryResult.RETRY
                : ExceptionHandler.Interceptor.RetryResult.ABORT;
          }
          return null;
        }
      };
  private static final ExceptionHandler EXCEPTION_HANDLER = ExceptionHandler.builder()
      .abortOn(RuntimeException.class, DatastoreException.class)
      .interceptor(EXCEPTION_HANDLER_INTERCEPTOR).build();

  private final DatastoreServiceOptions options;
  private final Datastore datastore;
  private final RetryParams retryParams;

  DatastoreServiceImpl(DatastoreServiceOptions options, Datastore datastore) {
    this.options = options;
    this.datastore = datastore;
    retryParams = MoreObjects.firstNonNull(options.retryParams(), RetryParams.noRetries());
  }

  @Override
  public DatastoreServiceOptions options() {
    return options;
  }

  @Override
  public BatchWriter newBatchWriter(BatchWriteOption... options) {
    return new BatchWriterImpl(this, options);
  }

  @Override
  public Transaction newTransaction(TransactionOption... options) {
    return new TransactionImpl(this, options);
  }

  @Override
  public <T> QueryResult<T> run(Query<T> query) {
    return run(null, query);
  }

  <T> QueryResult<T> run(DatastoreV1.ReadOptions readOptionsPb, Query<T> query) {
    return new QueryResultImpl<>(this, readOptionsPb, query);
  }

  DatastoreV1.RunQueryResponse runQuery(final DatastoreV1.RunQueryRequest requestPb) {
    try {
      return RetryHelper.runWithRetries(new Callable<DatastoreV1.RunQueryResponse>() {
        @Override public DatastoreV1.RunQueryResponse call() throws DatastoreException {
          return datastore.runQuery(requestPb);
        }
      }, retryParams, EXCEPTION_HANDLER);
    } catch (RetryHelperException e) {
      throw DatastoreServiceException.translateAndThrow(e);
    }
  }

  @Override
  public Key allocateId(PartialKey key) {
    return allocateId(new PartialKey[]{key}).get(0);
  }

  @Override
  public List<Key> allocateId(PartialKey... keys) {
    if (keys.length == 0) {
      return Collections.emptyList();
    }
    DatastoreV1.AllocateIdsRequest.Builder requestPb = DatastoreV1.AllocateIdsRequest.newBuilder();
    for (PartialKey key : keys) {
      requestPb.addKey(trimNameOrId(key).toPb());
    }
    // TODO(ozarov): will need to populate "force" after b/18594027 is fixed.
    DatastoreV1.AllocateIdsResponse responsePb = allocateIds(requestPb.build());
    Iterator<DatastoreV1.Key> keyIterator = responsePb.getKeyList().iterator();
    ImmutableList.Builder builder = ImmutableList.builder().addAll(
        Iterators.transform(keyIterator, new Function<DatastoreV1.Key, Key>() {
          @Override
          public Key apply(DatastoreV1.Key keyPb) {
            return Key.fromPb(keyPb);
          }
        }));
    return builder.build();
  }

  DatastoreV1.AllocateIdsResponse allocateIds(final DatastoreV1.AllocateIdsRequest requestPb) {
    try {
      return RetryHelper.runWithRetries(new Callable<DatastoreV1.AllocateIdsResponse>() {
        @Override public DatastoreV1.AllocateIdsResponse call() throws DatastoreException {
          return datastore.allocateIds(requestPb);
        }
      }, retryParams, EXCEPTION_HANDLER);
    } catch (RetryHelperException e) {
      throw DatastoreServiceException.translateAndThrow(e);
    }
  }

  private PartialKey trimNameOrId(PartialKey key) {
    if (key instanceof Key) {
      return PartialKey.builder(key).build();
    }
    return key;
  }

  @Override
  public Entity add(PartialEntity entity) {
    return add(new PartialEntity[] {entity}).get(0);
  }

  @Override
  public List<Entity> add(PartialEntity... entities) {
    if (entities.length == 0) {
      return Collections.emptyList();
    }
    DatastoreV1.Mutation.Builder mutationPb = DatastoreV1.Mutation.newBuilder();
    Map<Key, Entity> completeEntities = new LinkedHashMap<>();
    for (PartialEntity entity : entities) {
      Entity completeEntity = null;
      if (entity instanceof  Entity) {
        completeEntity = (Entity) entity;
      } else if (entity.key() instanceof Key) {
        completeEntity = entity.toEntity((Key) entity.key());
      }
      if (completeEntity != null) {
        if (completeEntities.put(completeEntity.key(), completeEntity) != null) {
          throw DatastoreServiceException.throwInvalidRequest(
              "Duplicate entity with the key %s", entity.key());
        }
        mutationPb.addInsert(completeEntity.toPb());
      } else {
        Preconditions.checkArgument(entity.hasKey(), "entity %s is missing a key", entity);
        mutationPb.addInsertAutoId(entity.toPb());
      }
    }
    DatastoreV1.CommitResponse commitResponse = commitMutation(mutationPb);
    Iterator<DatastoreV1.Key> allocatedKeys =
        commitResponse.getMutationResult().getInsertAutoIdKeyList().iterator();
    ImmutableList.Builder<Entity> responseBuilder = ImmutableList.builder();
    for (PartialEntity entity : entities) {
      PartialKey key = entity.key();
      Entity completeEntity = completeEntities.get(key);
      if (completeEntity != null) {
        responseBuilder.add(completeEntity);
      } else {
        responseBuilder.add(entity.toEntity(Key.fromPb(allocatedKeys.next())));
      }
    }
    return responseBuilder.build();
  }

  @Override
  public Entity get(Key key) {
    return Iterators.getNext(get(new Key[]{key}), null);
  }

  @Override
  public Iterator<Entity> get(Key... keys) {
    return get(null, keys);
  }

  Iterator<Entity> get(DatastoreV1.ReadOptions readOptionsPb, final Key... keys) {
    if (keys.length == 0) {
      return Collections.emptyIterator();
    }
    DatastoreV1.LookupRequest.Builder requestPb = DatastoreV1.LookupRequest.newBuilder();
    if (readOptionsPb != null) {
      requestPb.setReadOptions(readOptionsPb);
    }
    for (Key k : Sets.newLinkedHashSet(Arrays.asList(keys))) {
      requestPb.addKey(k.toPb());
    }
    return new ResultsIterator(requestPb);
  }

  final class ResultsIterator extends AbstractIterator<Entity> {

    private final DatastoreV1.LookupRequest.Builder requestPb;
    Iterator<DatastoreV1.EntityResult> iter;

    ResultsIterator(DatastoreV1.LookupRequest.Builder requestPb) {
      this.requestPb = requestPb;
      loadResults();
    }

    private void loadResults() {
      DatastoreV1.LookupResponse responsePb = lookup(requestPb.build());
      iter = responsePb.getFoundList().iterator();
      requestPb.clearKey();
      if (responsePb.getDeferredCount() > 0) {
        requestPb.addAllKey(responsePb.getDeferredList());
      }
    }

    @Override
    protected Entity computeNext() {
      if (iter.hasNext()) {
        return Entity.fromPb(iter.next().getEntity());
      }
      while (!iter.hasNext()) {
        if (requestPb.getKeyCount() == 0) {
          return endOfData();
        }
        loadResults();
      }
      return Entity.fromPb(iter.next().getEntity());
    }
  }

  DatastoreV1.LookupResponse lookup(final DatastoreV1.LookupRequest requestPb) {
    try {
      return RetryHelper.runWithRetries(new Callable<DatastoreV1.LookupResponse>() {
        @Override public DatastoreV1.LookupResponse call() throws DatastoreException {
          return datastore.lookup(requestPb);
        }
      }, retryParams, EXCEPTION_HANDLER);
    } catch (RetryHelperException e) {
      throw DatastoreServiceException.translateAndThrow(e);
    }
  }

  @Override
    public void add(Entity... entities) {
    if (entities.length > 0) {
      DatastoreV1.Mutation.Builder mutationPb = DatastoreV1.Mutation.newBuilder();
      Set<Key> keys = new LinkedHashSet<>();
      for (Entity entity : entities) {
        if (!keys.add(entity.key())) {
          throw DatastoreServiceException.throwInvalidRequest(
              "Duplicate entity with the key %s", entity.key());
        }
        mutationPb.addInsert(entity.toPb());
      }
      commitMutation(mutationPb);
    }
  }

  @Override
  public void update(Entity... entities) {
    if (entities.length > 0) {
      DatastoreV1.Mutation.Builder mutationPb = DatastoreV1.Mutation.newBuilder();
      Map<Key, Entity> dedupEntities = new LinkedHashMap<>();
      for (Entity entity : entities) {
        dedupEntities.put(entity.key(), entity);
      }
      for (Entity entity : dedupEntities.values()) {
        mutationPb.addUpdate(entity.toPb());
      }
      commitMutation(mutationPb);
    }
  }

  @Override
  public void put(Entity... entities) {
    if (entities.length > 0) {
      DatastoreV1.Mutation.Builder mutationPb = DatastoreV1.Mutation.newBuilder();
      Map<Key, Entity> dedupEntities = new LinkedHashMap<>();
      for (Entity entity : entities) {
        dedupEntities.put(entity.key(), entity);
      }
      for (Entity e : dedupEntities.values()) {
        mutationPb.addUpsert(e.toPb());
      }
      commitMutation(mutationPb);
    }
  }

  @Override
  public void delete(Key... keys) {
    if (keys.length > 0) {
      DatastoreV1.Mutation.Builder mutationPb = DatastoreV1.Mutation.newBuilder();
      Set<Key> dedupKeys = new LinkedHashSet<>(Arrays.asList(keys));
      for (Key key : dedupKeys) {
        mutationPb.addDelete(key.toPb());
      }
      commitMutation(mutationPb);
    }
  }

  private DatastoreV1.CommitResponse commitMutation(DatastoreV1.Mutation.Builder mutationPb) {
    if (options.force()) {
      mutationPb.setForce(true);
    }
    DatastoreV1.CommitRequest.Builder requestPb = DatastoreV1.CommitRequest.newBuilder();
    requestPb.setMode(DatastoreV1.CommitRequest.Mode.NON_TRANSACTIONAL);
    requestPb.setMutation(mutationPb);
    return commit(requestPb.build());
  }

  DatastoreV1.CommitResponse commit(final DatastoreV1.CommitRequest requestPb) {
    try {
      return RetryHelper.runWithRetries(new Callable<DatastoreV1.CommitResponse>() {
        @Override public DatastoreV1.CommitResponse call() throws DatastoreException {
          return datastore.commit(requestPb);
        }
      }, retryParams, EXCEPTION_HANDLER);
    } catch (RetryHelperException e) {
      throw DatastoreServiceException.translateAndThrow(e);
    }
  }

  ByteString requestTransactionId(DatastoreV1.BeginTransactionRequest.Builder requestPb) {
    return beginTransaction(requestPb.build()).getTransaction();
  }

  DatastoreV1.BeginTransactionResponse beginTransaction(
      final DatastoreV1.BeginTransactionRequest requestPb) {
    try {
      return RetryHelper.runWithRetries(new Callable<DatastoreV1.BeginTransactionResponse>() {
        @Override
        public DatastoreV1.BeginTransactionResponse call() throws DatastoreException {
          return datastore.beginTransaction(requestPb);
        }
      }, retryParams, EXCEPTION_HANDLER);
    } catch (RetryHelperException e) {
      throw DatastoreServiceException.translateAndThrow(e);
    }
  }

  void rollbackTransaction(ByteString transaction) {
    DatastoreV1.RollbackRequest.Builder requestPb = DatastoreV1.RollbackRequest.newBuilder();
    requestPb.setTransaction(transaction);
    rollback(requestPb.build());
  }

  DatastoreV1.RollbackResponse rollback(final DatastoreV1.RollbackRequest requestPb) {
    try {
      return RetryHelper.runWithRetries(new Callable<DatastoreV1.RollbackResponse>() {
        @Override public DatastoreV1.RollbackResponse call() throws DatastoreException {
          return datastore.rollback(requestPb);
        }
      }, retryParams, EXCEPTION_HANDLER);
    } catch (RetryHelperException e) {
      throw DatastoreServiceException.translateAndThrow(e);
    }
  }
}