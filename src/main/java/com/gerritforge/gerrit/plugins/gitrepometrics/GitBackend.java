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

import org.apache.commons.codec.digest.DigestUtils;

public enum GitBackend {
  GERRIT {
    @Override
    public String repoPath(String projectName) {
      return projectName;
    }
  },

  GITLAB {
    @Override
    public String repoPath(String projectName) {
      String sha256OfProjectName = DigestUtils.sha256Hex(projectName);
      return String.format(
          "%s/%s/%s",
          sha256OfProjectName.substring(0, 2),
          sha256OfProjectName.substring(2, 4),
          sha256OfProjectName);
    }
  };

  abstract String repoPath(String projectName);
}
