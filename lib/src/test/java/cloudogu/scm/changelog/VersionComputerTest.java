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
