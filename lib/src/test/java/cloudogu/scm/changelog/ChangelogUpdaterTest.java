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

import com.google.common.base.Joiner;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChangelogUpdaterTest {

  private List<String> source;
  private Path changelogFile;
  private ChangelogUpdater updater;

  private void prepare(Path folder, String source, String target) throws IOException {
    Path changelogSource = resource(target);

    this.source = Files.readAllLines(changelogSource);
    Path changelogEntriesDirectory = resource(source);

    changelogFile = folder.resolve("CHANGELOG.md");
    Files.copy(changelogSource, changelogFile);

    updater = new ChangelogUpdater(
      changelogFile,
      changelogEntriesDirectory,
      Instant.parse("2020-12-15T10:15:30.00Z")
    ).withVersion("2.12.0");
  }

  @Test
  void shouldCreateCorrectChangelog(@TempDir Path folder) throws IOException {
    prepare(folder, "multiple", "changelog.md");
    updater.update();

    List<String> changelog = Files.readAllLines(changelogFile);
    assertThat(changelog)
      // ensure that we not remove any lines
      .containsAll(source)
      // ensure versions are in the right order
      .containsSubsequence("## 2.12.0 - 2020-12-15", "## 2.11.1 - 2020-12-07", "## 2.11.0 - 2020-12-04")
      // ensure types are in the right order
      .containsSubsequence("### Added", "### Changed", "### Fixed")
      // contains all features
      .containsAll(lines("expected_added.md"))
      .containsAll(lines("expected_changed.md"))
      .containsAll(lines("expected_fixed.md"));
  }

  @Test
  void shouldNormalizeMixedCase(@TempDir Path folder) throws IOException {
    prepare(folder, "mixedcase", "changelog.md");
    updater.update();

    List<String> changelog = Files.readAllLines(changelogFile);
    System.out.println(Joiner.on("\n").join(changelog));
    assertThat(changelog)
      // ensure types are in the right order
      .containsSubsequence("### Added", "### Changed", "### Fixed")
      .doesNotContain("### added", "### changed", "### fixed")
      .doesNotContain("### ADDED", "### CHANGED", "### FIXED")
      // contains all features
      .containsAll(lines("expected_added.md"))
      .containsAll(lines("expected_changed.md"))
      .containsAll(lines("expected_fixed.md"));
  }

  @Test
  void shouldCreateCorrectChangelogWithLinks(@TempDir Path folder) throws IOException {
    prepare(folder, "multiple", "changelog_with_links.md");
    updater
      .withVersionUrls("https://www.scm-manager.org/download/{0}")
      .update();

    List<String> changelog = Files.readAllLines(changelogFile);
    assertThat(changelog)
      .containsSubsequence("## [2.12.0] - 2020-12-15", "## [2.11.1] - 2020-12-07", "## [2.11.0] - 2020-12-04")
      .containsSequence(
        "[2.11.0]: https://www.scm-manager.org/download/2.11.0",
        "[2.11.1]: https://www.scm-manager.org/download/2.11.1",
        "[2.12.0]: https://www.scm-manager.org/download/2.12.0"
      );
  }

  @SuppressWarnings("UnstableApiUsage")
  private Path resource(String path) {
    return Paths.get(Resources.getResource(path).getFile());
  }

  private List<String> lines(String path) throws IOException {
    return Files.readAllLines(resource(path));
  }

}
