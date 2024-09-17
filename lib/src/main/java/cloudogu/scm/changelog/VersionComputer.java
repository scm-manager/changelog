/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package cloudogu.scm.changelog;

import java.util.List;
import java.util.Map;

class VersionComputer {
  public String computeNextVersionNumber(Map<String, List<Changelog.Change>> newEntries, Changelog oldChangelog) {
    if (oldChangelog.getVersions().isEmpty()) {
      return "1.0.0";
    }
    String[] lastVersion = oldChangelog.getVersions().get(0).getNumber().split("\\.");
    if (lastVersion.length != 3) {
      throw new IllegalStateException("Cannot handle versions without major, minor and patch parts");
    }
    if (newEntries.containsKey(ChangeEntries.CHANGED) || newEntries.containsKey(ChangeEntries.ADDED)) {
      int nextMinorVersion = Integer.parseInt(lastVersion[1]) + 1;
      return String.format("%s.%s.%s", lastVersion[0], nextMinorVersion, 0);
    } else {
      int nextPatchVersion = Integer.parseInt(lastVersion[2]) + 1;
      return String.format("%s.%s.%s", lastVersion[0], lastVersion[1], nextPatchVersion);
    }
  }
}
