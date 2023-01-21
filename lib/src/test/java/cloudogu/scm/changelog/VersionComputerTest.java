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

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class VersionComputerTest {

  @Test
  void shouldReturnInitialVersionWithoutOldReleases() {
    Changelog oldChangelog = new Changelog(emptyList(), emptyList(), emptyList());
    String nextVersionNumber = new VersionComputer().computeNextVersionNumber(Collections.emptyMap(), oldChangelog);

    assertThat(nextVersionNumber).isEqualTo("1.0.0");
  }

  @Test
  void shouldUpdateToNextMinorVersionWithAddedChanges() {
    Changelog oldChangelog = createOldChangelog();
    Map<String, List<Changelog.Change>> changes = Map.of(
      "Added", emptyList(),
      "Fixed", emptyList()
    );

    String nextVersionNumber = new VersionComputer().computeNextVersionNumber(changes, oldChangelog);

    assertThat(nextVersionNumber).isEqualTo("2.43.0");
  }

  @Test
  void shouldUpdateToNextMinorVersionWithChangedChanges() {
    Changelog oldChangelog = createOldChangelog();
    Map<String, List<Changelog.Change>> changes = Map.of(
      "Changed", emptyList(),
      "Fixed", emptyList()
      );

    String nextVersionNumber = new VersionComputer().computeNextVersionNumber(changes, oldChangelog);

    assertThat(nextVersionNumber).isEqualTo("2.43.0");
  }

  @Test
  void shouldUpdateToNextPatchVersionWithFixedChanges() {
    Changelog oldChangelog = createOldChangelog();

    String nextVersionNumber = new VersionComputer().computeNextVersionNumber(Map.of("Fixed", emptyList()), oldChangelog);

    assertThat(nextVersionNumber).isEqualTo("2.42.3");
  }

  private Changelog createOldChangelog() {
    List<Changelog.Version> oldVersions = asList(
      new Changelog.Version("2.42.2", null, null),
      new Changelog.Version("2.42.1", null, null),
      new Changelog.Version("2.42.0", null, null)
    );
    return new Changelog(emptyList(), oldVersions, emptyList());
  }
}
