/*
 * Copyright 2015 Google Inc. All Rights Reserved.
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

/**
 * A client to Google Cloud BigQuery.
 *
 * <p>A simple usage example showing how to create a table if it does not exist and load data into
 * it. For the complete source code see
 * <a href="https://github.com/GoogleCloudPlatform/gcloud-java/tree/master/gcloud-java-examples/src/main/java/com/google/cloud/examples/bigquery/snippets/CreateTableAndLoadData.java">
 * CreateTableAndLoadData.java</a>.
 * <pre> {@code
 * BigQuery bigquery = BigQueryOptions.defaultInstance().service();
 * TableId tableId = TableId.of("dataset", "table");
 * Table table = bigquery.getTable(tableId);
 * if (table == null) {
 *   System.out.println("Creating table " + tableId);
 *   Field integerField = Field.of("fieldName", Field.Type.integer());
 *   Schema schema = Schema.of(integerField);
 *   table = bigquery.create(TableInfo.of(tableId, StandardTableDefinition.of(schema)));
 * }
 * System.out.println("Loading data into table " + tableId);
 * Job loadJob = table.load(FormatOptions.csv(), "gs://bucket/path");
 * while (!loadJob.isDone()) {
 *   Thread.sleep(1000L);
 * }
 * if (loadJob.status().error() != null) {
 *   System.out.println("Job completed with errors");
 * } else {
 *   System.out.println("Job succeeded");
 * }}</pre>
 *
 * @see <a href="https://cloud.google.com/bigquery/">Google Cloud BigQuery</a>
 */
package com.google.cloud.bigquery;
