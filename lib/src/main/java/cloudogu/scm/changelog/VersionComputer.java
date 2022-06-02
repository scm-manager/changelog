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
