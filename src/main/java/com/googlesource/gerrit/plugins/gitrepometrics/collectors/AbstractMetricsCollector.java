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
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import org.eclipse.jgit.internal.storage.file.FileRepository;

abstract class AbstractMetricsCollector implements MetricsCollector {
  private final ExecutorService executorService;
  private final GitRepoMetric collectionTime;

  protected AbstractMetricsCollector(ExecutorService executorService, String collectorPrefix) {
    this.executorService = executorService;
    this.collectionTime =
        new GitRepoMetric(
            String.format("%sMetricsCollectionTime", collectorPrefix),
            "Timestamp at which metrics were collected",
            "Milliseconds");
  }

  @Override
  public void collect(
      FileRepository repository,
      String projectName,
      Consumer<HashMap<GitRepoMetric, Long>> populateMetrics) {
    executorService.submit(
        () -> {
          HashMap<GitRepoMetric, Long> computed = computeMetrics(repository, projectName);
          if (!computed.isEmpty()) {
            computed.put(collectionTime, System.currentTimeMillis());
            populateMetrics.accept(computed);
          }
        });
  }

  @Override
  public ImmutableList<GitRepoMetric> availableMetrics() {
    return ImmutableList.<GitRepoMetric>builder()
        .addAll(collectorMetrics())
        .add(collectionTime)
        .build();
  }

  protected abstract HashMap<GitRepoMetric, Long> computeMetrics(
      FileRepository repository, String projectName);

  protected abstract ImmutableList<GitRepoMetric> collectorMetrics();
}
