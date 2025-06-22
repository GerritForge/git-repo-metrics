// Copyright (C) 2025 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.gitrepometrics.collectors;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.gitrepometrics.UpdateGitMetricsExecutor;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import org.eclipse.jgit.internal.storage.file.FileRepository;

public class MetaMetricsCollector implements MetricsCollector {
  private static final GitRepoMetric metricsCollectionTime =
      new GitRepoMetric(
          "metricsCollectionTime", "Timestamp at which metrics were collected", "Count");
  private static final ImmutableList<GitRepoMetric> availableMetrics =
      ImmutableList.of(metricsCollectionTime);

  private final ExecutorService executorService;

  @Inject
  MetaMetricsCollector(@UpdateGitMetricsExecutor ScheduledExecutorService executorService) {
    this.executorService = executorService;
  }

  @Override
  public void collect(
      FileRepository repository,
      String projectName,
      Consumer<HashMap<GitRepoMetric, Long>> populateMetrics) {
    executorService.submit(
        () -> {
          long collectionTime = System.currentTimeMillis();
          HashMap<GitRepoMetric, Long> metrics = new HashMap<>(1);
          metrics.put(metricsCollectionTime, collectionTime);
          populateMetrics.accept(metrics);
        });
  }

  @Override
  public String getMetricsCollectorName() {
    return "meta-statistics";
  }

  @Override
  public ImmutableList<GitRepoMetric> availableMetrics() {
    return availableMetrics;
  }
}
