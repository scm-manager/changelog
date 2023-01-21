/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
