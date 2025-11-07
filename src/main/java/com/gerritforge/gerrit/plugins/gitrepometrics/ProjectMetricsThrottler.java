// Copyright (C) 2025 GerritForge, Inc.
//
// Licensed under the BSL 1.1 (the "License");
// you may not use this file except in compliance with the License.
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.gerritforge.gerrit.plugins.gitrepometrics;

import com.google.common.util.concurrent.RateLimiter;
import com.google.inject.Inject;
import java.util.concurrent.ConcurrentHashMap;

class ProjectMetricsThrottler implements ProjectMetricsLimiter {
  private double rate;

  @Inject
  ProjectMetricsThrottler(GitRepoMetricsConfig repoMetricsConfig) {
    this.rate = (double) 1000L / repoMetricsConfig.getGracePeriodMs();
  }

  private ConcurrentHashMap<String, RateLimiter> projectsRateLimiters = new ConcurrentHashMap<>();

  @Override
  public void acquire(String projectName) {
    projectsRateLimiters.computeIfAbsent(projectName, (p) -> RateLimiter.create(rate)).acquire();
  }
}
