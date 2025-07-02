// Copyright (C) 2024 The Android Open Source Project
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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.flogger.FluentLogger;
import com.google.inject.Inject;
import com.googlesource.gerrit.plugins.gitrepometrics.UpdateGitMetricsExecutor;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;

public class GitRefsMetricsCollector extends AbstractMetricsCollector {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  @VisibleForTesting
  protected static final GitRepoMetric combinedRefsSha1 =
      new GitRepoMetric("combinedRefsSha1", "Numeric value of combined refs SHA-1's", "Number");

  private static final ImmutableList<GitRepoMetric> availableMetrics =
      ImmutableList.of(combinedRefsSha1);

  @Inject
  GitRefsMetricsCollector(@UpdateGitMetricsExecutor ScheduledExecutorService executorService) {
    super(executorService, "refs", availableMetrics);
  }

  @Override
  protected HashMap<GitRepoMetric, Long> computeMetrics(
      FileRepository repository, String projectName) {
    HashMap<GitRepoMetric, Long> metrics = new HashMap<>();
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      repository.getRefDatabase().getRefs().stream()
          .filter(ref -> !ref.isSymbolic())
          .sorted(Comparator.comparing(Ref::getName))
          .forEach(ref -> md.update(ref.getObjectId().toString().getBytes(UTF_8)));
      int sha1Int = truncateHashToInt(md.digest());

      metrics.put(combinedRefsSha1, (long) sha1Int);
    } catch (NoSuchAlgorithmException e) {
      logger.atSevere().withCause(e).log(
          "Could not obtain SHA-1 implementation will not compute the combinedRefsSha1"
              + " metric");
    } catch (IOException e) {
      logger.atSevere().withCause(e).log("Computing combinedRefsSha1 failed. Will retry next time");
    }
    return metrics;
  }

  // Source
  // http://www.java2s.com/example/java-utility-method/sha1/sha1hashint-string-text-d6c0e.html
  private static int truncateHashToInt(byte[] bytes) {
    int offset = bytes[bytes.length - 1] & 0x0f;
    return (bytes[offset] & (0x7f << 24))
        | (bytes[offset + 1] & (0xff << 16))
        | (bytes[offset + 2] & (0xff << 8))
        | (bytes[offset + 3] & 0xff);
  }
}
