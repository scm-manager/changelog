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

import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ChangelogUpdaterTest {

  @Test
  void shouldCreateCorrectChangelog(@TempDir Path folder) throws IOException {
    final Path changelogResourceFile = Paths.get(Resources.getResource("changelog.md").getFile());
    final Path expectedChangelogResourceFile = Paths.get(Resources.getResource("expected_changelog.md").getFile());
    final Path unreleasedChangelogEntriesResourceFile = Paths.get(Resources.getResource("multiple").getFile());

    final Path changelogFile = folder.resolve("CHANGELOG.md");
    Files.copy(changelogResourceFile, changelogFile);

    new ChangelogUpdater(changelogFile, unreleasedChangelogEntriesResourceFile, "2.12.0", Instant.parse("2020-12-15T10:15:30.00Z"))
      .update();

    final String actualChangelog = readString(changelogFile);
    final String expectedChangelog = readString(expectedChangelogResourceFile);
    assertThat(actualChangelog).isEqualTo(expectedChangelog);
  }

  @Test
  void shouldCreateCorrectChangelogWithLinks(@TempDir Path folder) throws IOException {
    final Path changelogResourceFile = Paths.get(Resources.getResource("changelog_with_links.md").getFile());
    final Path expectedChangelogResourceFile = Paths.get(Resources.getResource("expected_changelog_with_links.md").getFile());
    final Path unreleasedChangelogEntriesResourceFile = Paths.get(Resources.getResource("multiple").getFile());

    final Path changelogFile = folder.resolve("CHANGELOG.md");
    Files.copy(changelogResourceFile, changelogFile);

    new ChangelogUpdater(changelogFile, unreleasedChangelogEntriesResourceFile, "2.12.0", Instant.parse("2020-12-15T10:15:30.00Z"))
      .withVersionUrls("https://www.scm-manager.org/download/{0}")
      .update();

    final String actualChangelog = readString(changelogFile);
    final String expectedChangelog = readString(expectedChangelogResourceFile);
    assertThat(actualChangelog).isEqualTo(expectedChangelog);
  }

  private String readString(Path path) throws IOException {
    return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
  }

}
