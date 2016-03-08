/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gcloud.compute;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.gcloud.RetryHelper.runWithRetries;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gcloud.BaseService;
import com.google.gcloud.Page;
import com.google.gcloud.PageImpl;
import com.google.gcloud.PageImpl.NextPageFetcher;
import com.google.gcloud.RetryHelper;
import com.google.gcloud.spi.ComputeRpc;

import java.util.Map;
import java.util.concurrent.Callable;

final class ComputeImpl extends BaseService<ComputeOptions> implements Compute {

  private static class DiskTypePageFetcher implements NextPageFetcher<DiskType> {

    private static final long serialVersionUID = -5253916264932522976L;
    private final Map<ComputeRpc.Option, ?> requestOptions;
    private final ComputeOptions serviceOptions;
    private final String zone;

    DiskTypePageFetcher(String zone, ComputeOptions serviceOptions, String cursor,
        Map<ComputeRpc.Option, ?> optionMap) {
      this.requestOptions =
          PageImpl.nextRequestOptions(ComputeRpc.Option.PAGE_TOKEN, cursor, optionMap);
      this.serviceOptions = serviceOptions;
      this.zone = zone;
    }

    @Override
    public Page<DiskType> nextPage() {
      return listDiskTypes(zone, serviceOptions, requestOptions);
    }
  }

  private static class AggregatedDiskTypePageFetcher implements NextPageFetcher<DiskType> {

    private static final long serialVersionUID = -1664743503750307996L;
    private final Map<ComputeRpc.Option, ?> requestOptions;
    private final ComputeOptions serviceOptions;

    AggregatedDiskTypePageFetcher(ComputeOptions serviceOptions, String cursor,
        Map<ComputeRpc.Option, ?> optionMap) {
      this.requestOptions =
          PageImpl.nextRequestOptions(ComputeRpc.Option.PAGE_TOKEN, cursor, optionMap);
      this.serviceOptions = serviceOptions;
    }

    @Override
    public Page<DiskType> nextPage() {
      return listDiskTypes(serviceOptions, requestOptions);
    }
  }

  private static class MachineTypePageFetcher implements NextPageFetcher<MachineType> {

    private static final long serialVersionUID = -5048133000517001933L;
    private final Map<ComputeRpc.Option, ?> requestOptions;
    private final ComputeOptions serviceOptions;
    private final String zone;

    MachineTypePageFetcher(String zone, ComputeOptions serviceOptions, String cursor,
        Map<ComputeRpc.Option, ?> optionMap) {
      this.requestOptions =
          PageImpl.nextRequestOptions(ComputeRpc.Option.PAGE_TOKEN, cursor, optionMap);
      this.serviceOptions = serviceOptions;
      this.zone = zone;
    }

    @Override
    public Page<MachineType> nextPage() {
      return listMachineTypes(zone, serviceOptions, requestOptions);
    }
  }

  private static class AggregatedMachineTypePageFetcher implements NextPageFetcher<MachineType> {

    private static final long serialVersionUID = 2919227789802660026L;
    private final Map<ComputeRpc.Option, ?> requestOptions;
    private final ComputeOptions serviceOptions;

    AggregatedMachineTypePageFetcher(ComputeOptions serviceOptions, String cursor,
        Map<ComputeRpc.Option, ?> optionMap) {
      this.requestOptions =
          PageImpl.nextRequestOptions(ComputeRpc.Option.PAGE_TOKEN, cursor, optionMap);
      this.serviceOptions = serviceOptions;
    }

    @Override
    public Page<MachineType> nextPage() {
      return listMachineTypes(serviceOptions, requestOptions);
    }
  }

  private static class RegionPageFetcher implements NextPageFetcher<Region> {

    private static final long serialVersionUID = 4180147045485258863L;
    private final Map<ComputeRpc.Option, ?> requestOptions;
    private final ComputeOptions serviceOptions;

    RegionPageFetcher(ComputeOptions serviceOptions, String cursor,
        Map<ComputeRpc.Option, ?> optionMap) {
      this.requestOptions =
          PageImpl.nextRequestOptions(ComputeRpc.Option.PAGE_TOKEN, cursor, optionMap);
      this.serviceOptions = serviceOptions;
    }

    @Override
    public Page<Region> nextPage() {
      return listRegions(serviceOptions, requestOptions);
    }
  }

  private static class ZonePageFetcher implements NextPageFetcher<Zone> {

    private static final long serialVersionUID = -3946202621600687597L;
    private final Map<ComputeRpc.Option, ?> requestOptions;
    private final ComputeOptions serviceOptions;

    ZonePageFetcher(ComputeOptions serviceOptions, String cursor,
        Map<ComputeRpc.Option, ?> optionMap) {
      this.requestOptions =
          PageImpl.nextRequestOptions(ComputeRpc.Option.PAGE_TOKEN, cursor, optionMap);
      this.serviceOptions = serviceOptions;
    }

    @Override
    public Page<Zone> nextPage() {
      return listZones(serviceOptions, requestOptions);
    }
  }

  private final ComputeRpc computeRpc;

  ComputeImpl(ComputeOptions options) {
    super(options);
    computeRpc = options.rpc();
  }

  @Override
  public DiskType getDiskType(final DiskTypeId diskTypeId, DiskTypeOption... options) {
    final Map<ComputeRpc.Option, ?> optionsMap = optionMap(options);
    try {
      com.google.api.services.compute.model.DiskType answer =
          runWithRetries(new Callable<com.google.api.services.compute.model.DiskType>() {
            @Override
            public com.google.api.services.compute.model.DiskType call() {
              return computeRpc.getDiskType(diskTypeId.zone(), diskTypeId.diskType(), optionsMap);
            }
          }, options().retryParams(), EXCEPTION_HANDLER);
      return answer == null ? null : DiskType.fromPb(answer);
    } catch (RetryHelper.RetryHelperException e) {
      throw ComputeException.translateAndThrow(e);
    }
  }

  @Override
  public DiskType getDiskType(String zone, String diskType, DiskTypeOption... options) {
    return getDiskType(DiskTypeId.of(zone, diskType), options);
  }

  @Override
  public Page<DiskType> listDiskTypes(String zone, DiskTypeListOption... options) {
    return listDiskTypes(zone, options(), optionMap(options));
  }

  private static Page<DiskType> listDiskTypes(final String zone,
      final ComputeOptions serviceOptions, final Map<ComputeRpc.Option, ?> optionsMap) {
    try {
      ComputeRpc.Tuple<String, Iterable<com.google.api.services.compute.model.DiskType>> result =
          runWithRetries(new Callable<ComputeRpc.Tuple<String,
              Iterable<com.google.api.services.compute.model.DiskType>>>() {
            @Override
            public ComputeRpc.Tuple<String,
                Iterable<com.google.api.services.compute.model.DiskType>> call() {
              return serviceOptions.rpc().listDiskTypes(zone, optionsMap);
            }
          }, serviceOptions.retryParams(), EXCEPTION_HANDLER);
      String cursor = result.x();
      Iterable<DiskType> diskTypes = Iterables.transform(
          result.y() == null ? ImmutableList.<com.google.api.services.compute.model.DiskType>of()
              : result.y(),
          new Function<com.google.api.services.compute.model.DiskType, DiskType>() {
            @Override
            public DiskType apply(com.google.api.services.compute.model.DiskType diskType) {
              return DiskType.fromPb(diskType);
            }
          });
      return new PageImpl<>(new DiskTypePageFetcher(zone, serviceOptions, cursor, optionsMap),
          cursor, diskTypes);
    } catch (RetryHelper.RetryHelperException e) {
      throw ComputeException.translateAndThrow(e);
    }
  }

  @Override
  public Page<DiskType> listDiskTypes(DiskTypeAggregatedListOption... options) {
    return listDiskTypes(options(), optionMap(options));
  }

  private static Page<DiskType> listDiskTypes(final ComputeOptions serviceOptions,
      final Map<ComputeRpc.Option, ?> optionsMap) {
    try {
      ComputeRpc.Tuple<String, Iterable<com.google.api.services.compute.model.DiskType>> result =
          runWithRetries(new Callable<ComputeRpc.Tuple<String,
              Iterable<com.google.api.services.compute.model.DiskType>>>() {
            @Override
            public ComputeRpc.Tuple<String,
                Iterable<com.google.api.services.compute.model.DiskType>> call() {
              return serviceOptions.rpc().listDiskTypes(optionsMap);
            }
          }, serviceOptions.retryParams(), EXCEPTION_HANDLER);
      String cursor = result.x();
      Iterable<DiskType> diskTypes = Iterables.transform(result.y(),
          new Function<com.google.api.services.compute.model.DiskType, DiskType>() {
            @Override
            public DiskType apply(com.google.api.services.compute.model.DiskType diskType) {
              return DiskType.fromPb(diskType);
            }
          });
      return new PageImpl<>(new AggregatedDiskTypePageFetcher(serviceOptions, cursor, optionsMap),
          cursor, diskTypes);
    } catch (RetryHelper.RetryHelperException e) {
      throw ComputeException.translateAndThrow(e);
    }
  }

  @Override
  public MachineType getMachineType(final MachineTypeId machineType, MachineTypeOption... options) {
    final Map<ComputeRpc.Option, ?> optionsMap = optionMap(options);
    try {
      com.google.api.services.compute.model.MachineType answer =
          runWithRetries(new Callable<com.google.api.services.compute.model.MachineType>() {
            @Override
            public com.google.api.services.compute.model.MachineType call() {
              return computeRpc.getMachineType(machineType.zone(), machineType.machineType(),
                  optionsMap);
            }
          }, options().retryParams(), EXCEPTION_HANDLER);
      return answer == null ? null : MachineType.fromPb(answer);
    } catch (RetryHelper.RetryHelperException e) {
      throw ComputeException.translateAndThrow(e);
    }
  }

  @Override
  public MachineType getMachineType(String zone, String machineType, MachineTypeOption... options) {
    return getMachineType(MachineTypeId.of(zone, machineType), options);
  }

  @Override
  public Page<MachineType> listMachineTypes(String zone, MachineTypeListOption... options) {
    return listMachineTypes(zone, options(), optionMap(options));
  }

  private static Page<MachineType> listMachineTypes(final String zone,
      final ComputeOptions serviceOptions, final Map<ComputeRpc.Option, ?> optionsMap) {
    try {
      ComputeRpc.Tuple<String, Iterable<com.google.api.services.compute.model.MachineType>> result =
          runWithRetries(new Callable<ComputeRpc.Tuple<String,
              Iterable<com.google.api.services.compute.model.MachineType>>>() {
            @Override
            public ComputeRpc.Tuple<String,
                Iterable<com.google.api.services.compute.model.MachineType>> call() {
              return serviceOptions.rpc().listMachineTypes(zone, optionsMap);
            }
          }, serviceOptions.retryParams(), EXCEPTION_HANDLER);
      String cursor = result.x();
      Iterable<MachineType> machineTypes = Iterables.transform(
          result.y() == null ? ImmutableList.<com.google.api.services.compute.model.MachineType>of()
              : result.y(),
          new Function<com.google.api.services.compute.model.MachineType, MachineType>() {
            @Override
            public MachineType apply(
                com.google.api.services.compute.model.MachineType machineType) {
              return MachineType.fromPb(machineType);
            }
          });
      return new PageImpl<>(new MachineTypePageFetcher(zone, serviceOptions, cursor, optionsMap),
          cursor, machineTypes);
    } catch (RetryHelper.RetryHelperException e) {
      throw ComputeException.translateAndThrow(e);
    }
  }

  @Override
  public Page<MachineType> listMachineTypes(MachineTypeAggregatedListOption... options) {
    return listMachineTypes(options(), optionMap(options));
  }

  private static Page<MachineType> listMachineTypes(final ComputeOptions serviceOptions,
      final Map<ComputeRpc.Option, ?> optionsMap) {
    try {
      ComputeRpc.Tuple<String, Iterable<com.google.api.services.compute.model.MachineType>> result =
          runWithRetries(new Callable<ComputeRpc.Tuple<String,
              Iterable<com.google.api.services.compute.model.MachineType>>>() {
            @Override
            public ComputeRpc.Tuple<String,
                Iterable<com.google.api.services.compute.model.MachineType>> call() {
              return serviceOptions.rpc().listMachineTypes(optionsMap);
            }
          }, serviceOptions.retryParams(), EXCEPTION_HANDLER);
      String cursor = result.x();
      Iterable<MachineType> machineTypes = Iterables.transform(result.y(),
          new Function<com.google.api.services.compute.model.MachineType, MachineType>() {
            @Override
            public MachineType apply(
                com.google.api.services.compute.model.MachineType machineType) {
              return MachineType.fromPb(machineType);
            }
          });
      return new PageImpl<>(
          new AggregatedMachineTypePageFetcher(serviceOptions, cursor, optionsMap), cursor,
          machineTypes);
    } catch (RetryHelper.RetryHelperException e) {
      throw ComputeException.translateAndThrow(e);
    }
  }

  @Override
  public Region getRegion(final String region, RegionOption... options) {
    final Map<ComputeRpc.Option, ?> optionsMap = optionMap(options);
    try {
      com.google.api.services.compute.model.Region answer =
          runWithRetries(new Callable<com.google.api.services.compute.model.Region>() {
            @Override
            public com.google.api.services.compute.model.Region call() {
              return computeRpc.getRegion(region, optionsMap);
            }
          }, options().retryParams(), EXCEPTION_HANDLER);
      return answer == null ? null : Region.fromPb(answer);
    } catch (RetryHelper.RetryHelperException e) {
      throw ComputeException.translateAndThrow(e);
    }
  }

  @Override
  public Page<Region> listRegions(RegionListOption... options) {
    return listRegions(options(), optionMap(options));
  }

  private static Page<Region> listRegions(final ComputeOptions serviceOptions,
      final Map<ComputeRpc.Option, ?> optionsMap) {
    try {
      ComputeRpc.Tuple<String, Iterable<com.google.api.services.compute.model.Region>> result =
          runWithRetries(new Callable<ComputeRpc.Tuple<String,
              Iterable<com.google.api.services.compute.model.Region>>>() {
            @Override
            public ComputeRpc.Tuple<String,
                Iterable<com.google.api.services.compute.model.Region>> call() {
              return serviceOptions.rpc().listRegions(optionsMap);
            }
          }, serviceOptions.retryParams(), EXCEPTION_HANDLER);
      String cursor = result.x();
      Iterable<Region> regions = Iterables.transform(
          result.y() == null ? ImmutableList.<com.google.api.services.compute.model.Region>of()
              : result.y(),
          new Function<com.google.api.services.compute.model.Region, Region>() {
            @Override
            public Region apply(com.google.api.services.compute.model.Region region) {
              return Region.fromPb(region);
            }
          });
      return new PageImpl<>(new RegionPageFetcher(serviceOptions, cursor, optionsMap), cursor,
          regions);
    } catch (RetryHelper.RetryHelperException e) {
      throw ComputeException.translateAndThrow(e);
    }
  }

  @Override
  public Zone getZone(final String zone, ZoneOption... options) {
    final Map<ComputeRpc.Option, ?> optionsMap = optionMap(options);
    try {
      com.google.api.services.compute.model.Zone answer =
          runWithRetries(new Callable<com.google.api.services.compute.model.Zone>() {
            @Override
            public com.google.api.services.compute.model.Zone call() {
              return computeRpc.getZone(zone, optionsMap);
            }
          }, options().retryParams(), EXCEPTION_HANDLER);
      return answer == null ? null : Zone.fromPb(answer);
    } catch (RetryHelper.RetryHelperException e) {
      throw ComputeException.translateAndThrow(e);
    }
  }

  @Override
  public Page<Zone> listZones(ZoneListOption... options) {
    return listZones(options(), optionMap(options));
  }

  private static Page<Zone> listZones(final ComputeOptions serviceOptions,
      final Map<ComputeRpc.Option, ?> optionsMap) {
    try {
      ComputeRpc.Tuple<String, Iterable<com.google.api.services.compute.model.Zone>> result =
          runWithRetries(new Callable<ComputeRpc.Tuple<String,
              Iterable<com.google.api.services.compute.model.Zone>>>() {
            @Override
            public ComputeRpc.Tuple<String,
                Iterable<com.google.api.services.compute.model.Zone>> call() {
              return serviceOptions.rpc().listZones(optionsMap);
            }
          }, serviceOptions.retryParams(), EXCEPTION_HANDLER);
      String cursor = result.x();
      Iterable<Zone> zones = Iterables.transform(
          result.y() == null ? ImmutableList.<com.google.api.services.compute.model.Zone>of()
              : result.y(),
          new Function<com.google.api.services.compute.model.Zone, Zone>() {
            @Override
            public Zone apply(com.google.api.services.compute.model.Zone zone) {
              return Zone.fromPb(zone);
            }
          });
      return new PageImpl<>(new ZonePageFetcher(serviceOptions, cursor, optionsMap), cursor, zones);
    } catch (RetryHelper.RetryHelperException e) {
      throw ComputeException.translateAndThrow(e);
    }
  }

  @Override
  public License getLicense(String license, LicenseOption... options) {
    return getLicense(LicenseId.of(license), options);
  }

  @Override
  public License getLicense(LicenseId license, LicenseOption... options) {
    final LicenseId completeId = license.setProjectId(options().projectId());
    final Map<ComputeRpc.Option, ?> optionsMap = optionMap(options);
    try {
      com.google.api.services.compute.model.License answer =
          runWithRetries(new Callable<com.google.api.services.compute.model.License>() {
            @Override
            public com.google.api.services.compute.model.License call() {
              return computeRpc.getLicense(completeId.project(), completeId.license(), optionsMap);
            }
          }, options().retryParams(), EXCEPTION_HANDLER);
      return answer == null ? null : License.fromPb(answer);
    } catch (RetryHelper.RetryHelperException e) {
      throw ComputeException.translateAndThrow(e);
    }
  }

  private Map<ComputeRpc.Option, ?> optionMap(Option... options) {
    Map<ComputeRpc.Option, Object> optionMap = Maps.newEnumMap(ComputeRpc.Option.class);
    for (Option option : options) {
      Object prev = optionMap.put(option.rpcOption(), option.value());
      checkArgument(prev == null, "Duplicate option %s", option);
    }
    return optionMap;
  }
}