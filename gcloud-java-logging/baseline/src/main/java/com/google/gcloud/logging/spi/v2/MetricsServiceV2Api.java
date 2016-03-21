/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/*
 * EDITING INSTRUCTIONS
 * This file was generated from the file
 * https://github.com/google/googleapis/blob/master/google/logging/v2/logging_metrics.proto
 * and updates to that file get reflected here through a refresh process.
 * For the short term, the refresh process will only be runnable by Google engineers.
 * Manual additions are allowed because the refresh process performs
 * a 3-way merge in order to preserve those manual additions. In order to not
 * break the refresh process, only certain types of modifications are
 * allowed.
 *
 * Allowed modifications - currently these are the only types allowed:
 * 1. New methods (these should be added to the end of the class)
 * 2. New imports
 * 3. Additional documentation between "manual edit" demarcations
 *
 * Happy editing!
 */

package com.google.gcloud.logging.spi.v2;

import com.google.api.gax.grpc.ApiCallable;
import com.google.api.gax.protobuf.PathTemplate;
import com.google.logging.v2.CreateLogMetricRequest;
import com.google.logging.v2.DeleteLogMetricRequest;
import com.google.logging.v2.GetLogMetricRequest;
import com.google.logging.v2.ListLogMetricsRequest;
import com.google.logging.v2.ListLogMetricsResponse;
import com.google.logging.v2.LogMetric;
import com.google.logging.v2.UpdateLogMetricRequest;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Manually-added imports: add custom (non-generated) imports after this point.

// AUTO-GENERATED DOCUMENTATION AND SERVICE - see instructions at the top of the file for editing.
/**
 * Service Description: See src/api/google/logging/v1/logging.proto for documentation
 *
 * <!-- manual edit -->
 * <!-- end manual edit -->
 */
@javax.annotation.Generated("by GAPIC")
public class MetricsServiceV2Api implements AutoCloseable {

  public static class ResourceNames {
    private ResourceNames() {}

    // =======================
    // ResourceNames Constants
    // =======================

    /**
     * A PathTemplate representing the fully-qualified path to represent
     * a project resource.
     *
     * <!-- manual edit -->
     * <!-- end manual edit -->
     */
    private static final PathTemplate PROJECT_PATH_TEMPLATE =
        PathTemplate.create("projects/{project}");

    /**
     * A PathTemplate representing the fully-qualified path to represent
     * a metric resource.
     *
     * <!-- manual edit -->
     * <!-- end manual edit -->
     */
    private static final PathTemplate METRIC_PATH_TEMPLATE =
        PathTemplate.create("projects/{project}/metrics/{metric}");

    // ==============================
    // Resource Name Helper Functions
    // ==============================

    /**
     * Formats a string containing the fully-qualified path to represent
     * a project resource.
     *
     * <!-- manual edit -->
     * <!-- end manual edit -->
     */
    public static final String formatProjectPath(String project) {
      return PROJECT_PATH_TEMPLATE.instantiate("project", project);
    }

    /**
     * Formats a string containing the fully-qualified path to represent
     * a metric resource.
     *
     * <!-- manual edit -->
     * <!-- end manual edit -->
     */
    public static final String formatMetricPath(String project, String metric) {
      return METRIC_PATH_TEMPLATE.instantiate("project", project, "metric", metric);
    }

    /**
     * Parses the project from the given fully-qualified path which
     * represents a project resource.
     *
     * <!-- manual edit -->
     * <!-- end manual edit -->
     */
    public static final String parseProjectFromProjectPath(String projectPath) {
      return PROJECT_PATH_TEMPLATE.parse(projectPath).get("project");
    }

    /**
     * Parses the project from the given fully-qualified path which
     * represents a metric resource.
     *
     * <!-- manual edit -->
     * <!-- end manual edit -->
     */
    public static final String parseProjectFromMetricPath(String metricPath) {
      return METRIC_PATH_TEMPLATE.parse(metricPath).get("project");
    }

    /**
     * Parses the metric from the given fully-qualified path which
     * represents a metric resource.
     *
     * <!-- manual edit -->
     * <!-- end manual edit -->
     */
    public static final String parseMetricFromMetricPath(String metricPath) {
      return METRIC_PATH_TEMPLATE.parse(metricPath).get("metric");
    }
  }

  // ========
  // Members
  // ========

  private final ManagedChannel channel;
  private final List<AutoCloseable> closeables = new ArrayList<>();

  private final ApiCallable<ListLogMetricsRequest, ListLogMetricsResponse> listLogMetricsCallable;
  private final ApiCallable<ListLogMetricsRequest, Iterable<LogMetric>>
      listLogMetricsIterableCallable;
  private final ApiCallable<GetLogMetricRequest, LogMetric> getLogMetricCallable;
  private final ApiCallable<CreateLogMetricRequest, LogMetric> createLogMetricCallable;
  private final ApiCallable<UpdateLogMetricRequest, LogMetric> updateLogMetricCallable;
  private final ApiCallable<DeleteLogMetricRequest, Empty> deleteLogMetricCallable;

  // ===============
  // Factory Methods
  // ===============

  /**
   * Constructs an instance of MetricsServiceV2Api with default settings.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   */
  public static MetricsServiceV2Api create() throws IOException {
    return create(MetricsServiceV2Settings.create());
  }

  /**
   * Constructs an instance of MetricsServiceV2Api, using the given settings. The channels are
   * created based on the settings passed in, or defaults for any settings that are not set.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   */
  public static MetricsServiceV2Api create(MetricsServiceV2Settings settings) throws IOException {
    return new MetricsServiceV2Api(settings);
  }

  /**
   * Constructs an instance of MetricsServiceV2Api, using the given settings. This is protected so
   * that it easy to make a subclass, but otherwise, the static factory methods should be preferred.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   */
  protected MetricsServiceV2Api(MetricsServiceV2Settings settings) throws IOException {
    this.channel = settings.getChannel();

    this.listLogMetricsCallable = settings.listLogMetricsMethod().build(settings);
    this.listLogMetricsIterableCallable =
        settings.listLogMetricsMethod().buildPageStreaming(settings);
    this.getLogMetricCallable = settings.getLogMetricMethod().build(settings);
    this.createLogMetricCallable = settings.createLogMetricMethod().build(settings);
    this.updateLogMetricCallable = settings.updateLogMetricMethod().build(settings);
    this.deleteLogMetricCallable = settings.deleteLogMetricMethod().build(settings);

    closeables.add(
        new Closeable() {
          @Override
          public void close() throws IOException {
            channel.shutdown();
          }
        });
  }

  // =============
  // Service Calls
  // =============

  // ----- listLogMetrics -----

  // AUTO-GENERATED DOCUMENTATION AND METHOD - see instructions at the top of the file for editing.
  /**
   * Lists logs-based metrics.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   */
  public Iterable<LogMetric> listLogMetrics(String projectName) {
    ListLogMetricsRequest request =
        ListLogMetricsRequest.newBuilder().setProjectName(projectName).build();
    return listLogMetrics(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD - see instructions at the top of the file for editing.
  /**
   * Lists logs-based metrics.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   *
   * @param request The request object containing all of the parameters for the API call.
   */
  public Iterable<LogMetric> listLogMetrics(ListLogMetricsRequest request) {
    return listLogMetricsIterableCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD - see instructions at the top of the file for editing.
  /**
   * Lists logs-based metrics.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   */
  public ApiCallable<ListLogMetricsRequest, Iterable<LogMetric>> listLogMetricsIterableCallable() {
    return listLogMetricsIterableCallable;
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD - see instructions at the top of the file for editing.
  /**
   * Lists logs-based metrics.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   */
  public ApiCallable<ListLogMetricsRequest, ListLogMetricsResponse> listLogMetricsCallable() {
    return listLogMetricsCallable;
  }

  // ----- getLogMetric -----

  // AUTO-GENERATED DOCUMENTATION AND METHOD - see instructions at the top of the file for editing.
  /**
   * Gets a logs-based metric.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   *
   * @param metricName The resource name of the desired metric.
   * Example: `"projects/my-project-id/metrics/my-metric-id"`.
   */
  public LogMetric getLogMetric(String metricName) {
    GetLogMetricRequest request =
        GetLogMetricRequest.newBuilder().setMetricName(metricName).build();

    return getLogMetric(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD - see instructions at the top of the file for editing.
  /**
   * Gets a logs-based metric.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   *
   * @param request The request object containing all of the parameters for the API call.
   */
  private LogMetric getLogMetric(GetLogMetricRequest request) {
    return getLogMetricCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD - see instructions at the top of the file for editing.
  /**
   * Gets a logs-based metric.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   */
  public ApiCallable<GetLogMetricRequest, LogMetric> getLogMetricCallable() {
    return getLogMetricCallable;
  }

  // ----- createLogMetric -----

  // AUTO-GENERATED DOCUMENTATION AND METHOD - see instructions at the top of the file for editing.
  /**
   * Creates a logs-based metric.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   *
   * @param projectName The resource name of the project in which to create the metric.
   * Example: `"projects/my-project-id"`.
   *
   * The new metric must be provided in the request.
   * @param metric The new logs-based metric, which must not have an identifier that
   * already exists.
   */
  public LogMetric createLogMetric(String projectName, LogMetric metric) {
    CreateLogMetricRequest request =
        CreateLogMetricRequest.newBuilder().setProjectName(projectName).setMetric(metric).build();

    return createLogMetric(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD - see instructions at the top of the file for editing.
  /**
   * Creates a logs-based metric.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   *
   * @param request The request object containing all of the parameters for the API call.
   */
  public LogMetric createLogMetric(CreateLogMetricRequest request) {
    return createLogMetricCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD - see instructions at the top of the file for editing.
  /**
   * Creates a logs-based metric.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   */
  public ApiCallable<CreateLogMetricRequest, LogMetric> createLogMetricCallable() {
    return createLogMetricCallable;
  }

  // ----- updateLogMetric -----

  // AUTO-GENERATED DOCUMENTATION AND METHOD - see instructions at the top of the file for editing.
  /**
   * Creates or updates a logs-based metric.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   *
   * @param metricName The resource name of the metric to update.
   * Example: `"projects/my-project-id/metrics/my-metric-id"`.
   *
   * The updated metric must be provided in the request and have the
   * same identifier that is specified in `metricName`.
   * If the metric does not exist, it is created.
   * @param metric The updated metric, whose name must be the same as the
   * metric identifier in `metricName`. If `metricName` does not
   * exist, then a new metric is created.
   */
  public LogMetric updateLogMetric(String metricName, LogMetric metric) {
    UpdateLogMetricRequest request =
        UpdateLogMetricRequest.newBuilder().setMetricName(metricName).setMetric(metric).build();

    return updateLogMetric(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD - see instructions at the top of the file for editing.
  /**
   * Creates or updates a logs-based metric.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   *
   * @param request The request object containing all of the parameters for the API call.
   */
  public LogMetric updateLogMetric(UpdateLogMetricRequest request) {
    return updateLogMetricCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD - see instructions at the top of the file for editing.
  /**
   * Creates or updates a logs-based metric.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   */
  public ApiCallable<UpdateLogMetricRequest, LogMetric> updateLogMetricCallable() {
    return updateLogMetricCallable;
  }

  // ----- deleteLogMetric -----

  // AUTO-GENERATED DOCUMENTATION AND METHOD - see instructions at the top of the file for editing.
  /**
   * Deletes a logs-based metric.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   *
   * @param metricName The resource name of the metric to delete.
   * Example: `"projects/my-project-id/metrics/my-metric-id"`.
   */
  public void deleteLogMetric(String metricName) {
    DeleteLogMetricRequest request =
        DeleteLogMetricRequest.newBuilder().setMetricName(metricName).build();

    deleteLogMetric(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD - see instructions at the top of the file for editing.
  /**
   * Deletes a logs-based metric.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   *
   * @param request The request object containing all of the parameters for the API call.
   */
  private void deleteLogMetric(DeleteLogMetricRequest request) {
    deleteLogMetricCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD - see instructions at the top of the file for editing.
  /**
   * Deletes a logs-based metric.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   */
  public ApiCallable<DeleteLogMetricRequest, Empty> deleteLogMetricCallable() {
    return deleteLogMetricCallable;
  }

  // ========
  // Cleanup
  // ========

  /**
   * Initiates an orderly shutdown in which preexisting calls continue but new calls are immediately
   * cancelled.
   *
   * <!-- manual edit -->
   * <!-- end manual edit -->
   */
  @Override
  public void close() throws Exception {
    for (AutoCloseable closeable : closeables) {
      closeable.close();
    }
  }

  // ========
  // Manually-added methods: add custom (non-generated) methods after this point.
  // ========

}