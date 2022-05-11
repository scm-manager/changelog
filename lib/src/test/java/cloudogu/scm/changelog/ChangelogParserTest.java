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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class ChangelogParserTest {

  private static final List<String> CHANGELOG = asList(
    "# Changelog",
    "All notable changes to this project will be documented in this file.",
    "",
    "The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),",
    "and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).",
    "",
    "## 2.11.1-rc1-2 - 2020-12-07 ",
    "### Fixed",
    "- Initialization of new git repository with master set as default branch ([#1467](https://github.com/scm-manager/scm-manager/issues/1467) and [#1470](https://github.com/scm-manager/scm-manager/pull/1470))",
    "",
    "## 2.11.0 - 2020-12-04",
    "",
    "###  Added ",
    "- \"Add\" tooltips to short links on repository overview ([#1441](https://github.com/scm-manager/scm-manager/pull/1441))",
    "-  Show the date of the last commit for branches in the frontend ([#1439](https://github.com/scm-manager/scm-manager/pull/1439))",
    "- Unify and add description to key view across user settings ([#1440](https://github.com/scm-manager/scm-manager/pull/1440))",
    "- Healthcheck for docker image ([#1428](https://github.com/scm-manager/scm-manager/issues/1428) and [#1454](https://github.com/scm-manager/scm-manager/issues/1454))",
    "- Tags can now be added and deleted through the ui ([#1456](https://github.com/scm-manager/scm-manager/pull/1456))",
    "- The ui now displays tag signatures ([#1456](https://github.com/scm-manager/scm-manager/pull/1456))",
    "- Repository import via URL for git ([#1460](https://github.com/scm-manager/scm-manager/pull/1460))",
    "- Repository import via URL for hg ([#1463](https://github.com/scm-manager/scm-manager/pull/1463))",
    "",
    "### Changed",
    "- Send mercurial hook callbacks over separate tcp socket instead of http ([#1416](https://github.com/scm-manager/scm-manager/pull/1416))",
    "",
    "### Fixed",
    "- Language detection of files with interpreter parameters e.g.: `#!/usr/bin/make -f` ([#1450](https://github.com/scm-manager/scm-manager/issues/1450))"
  );

  private static final List<String> CHANGELOG_WITH_EMPTY_VERSION = asList(
    "# Changelog",
    "All notable changes to this project will be documented in this file.",
    "",
    "The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),",
    "and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).",
    "",
    "## 2.11.2 - 2020-12-07 ",
    "### Fixed",
    "- Initialization of new git repository with master set as default branch ([#1467](https://github.com/scm-manager/scm-manager/issues/1467) and [#1470](https://github.com/scm-manager/scm-manager/pull/1470))",
    "",
    "## 2.11.1 - 2020-12-05",
    "",
    "## 2.11.0 - 2020-12-04",
    "",
    "###  Added ",
    "- Language detection of files with interpreter parameters e.g.: `#!/usr/bin/make -f` ([#1450](https://github.com/scm-manager/scm-manager/issues/1450))"
  );

  private static final List<String> CHANGELOG_WITH_LINKS = asList(
    "# Changelog",
    "All notable changes to this project will be documented in this file.",
    "",
    "The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),",
    "and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).",
    "",
    "## [2.11.1-rc1] - 2020-12-07 ",
    "### Fixed",
    "- Initialization of new git repository with master set as default branch ([#1467](https://github.com/scm-manager/scm-manager/issues/1467) and [#1470](https://github.com/scm-manager/scm-manager/pull/1470))",
    "",
    "## [2.11.0] - 2020-12-04",
    "",
    "###  Added ",
    "- Add tooltips to short links on repository overview ([#1441](https://github.com/scm-manager/scm-manager/pull/1441))",
    "-  Show the date of the last commit for branches in the frontend ([#1439](https://github.com/scm-manager/scm-manager/pull/1439))",
    "- Unify and add description to key view across user settings ([#1440](https://github.com/scm-manager/scm-manager/pull/1440))",
    "- Healthcheck for docker image ([#1428](https://github.com/scm-manager/scm-manager/issues/1428) and [#1454](https://github.com/scm-manager/scm-manager/issues/1454))",
    "- Tags can now be added and deleted through the ui ([#1456](https://github.com/scm-manager/scm-manager/pull/1456))",
    "- The ui now displays tag signatures ([#1456](https://github.com/scm-manager/scm-manager/pull/1456))",
    "- Repository import via URL for git ([#1460](https://github.com/scm-manager/scm-manager/pull/1460))",
    "- Repository import via URL for hg ([#1463](https://github.com/scm-manager/scm-manager/pull/1463))",
    "",
    "### Changed",
    "- Send mercurial hook callbacks over separate tcp socket instead of http ([#1416](https://github.com/scm-manager/scm-manager/pull/1416))",
    "",
    "### Fixed",
    "- Language detection of files with interpreter parameters e.g.: `#!/usr/bin/make -f` ([#1450](https://github.com/scm-manager/scm-manager/issues/1450))",
    "",
    "[2.11.0]: https://www.scm-manager.org/download/2.11.0",
    "[2.11.1-rc1]: https://www.scm-manager.org/download/2.11.1"
  );

  @Test
  void shouldParseHeader() {
    Changelog changelog = new ChangelogParser().parse(CHANGELOG_WITH_LINKS);

    assertThat(changelog.getHeader()).containsExactly(
      "# Changelog",
      "All notable changes to this project will be documented in this file.",
      "",
      "The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),",
      "and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).",
      ""
    );
  }

  @Test
  void shouldParseVersionHeader() {
    Changelog changelog = new ChangelogParser().parse(CHANGELOG);
    List<Changelog.Version> versions = changelog.getVersions();
    Assertions.assertThat(versions).isNotEmpty();
    assertThat(versions.get(0).getNumber()).isEqualTo("2.11.1-rc1-2");
    assertThat(versions.get(0).getDate()).isEqualTo("2020-12-07T00:00:00.00Z");
    assertThat(versions.get(1).getNumber()).isEqualTo("2.11.0");
    assertThat(versions.get(1).getDate()).isEqualTo("2020-12-04T00:00:00.00Z");
  }

  @Test
  void shouldParseVersionWithoutChanges() {
    Changelog changelog = new ChangelogParser().parse(CHANGELOG_WITH_EMPTY_VERSION);
    List<Changelog.Version> versions = changelog.getVersions();
    Assertions.assertThat(versions).isNotEmpty();
    assertThat(versions.get(1).getNumber()).isEqualTo("2.11.1");
    assertThat(versions.get(1).getChanges()).isEmpty();
  }

  @Test
  void shouldParseVersionHeaderWithLinks() {
    Changelog changelog = new ChangelogParser().parse(CHANGELOG_WITH_LINKS);
    List<Changelog.Version> versions = changelog.getVersions();
    Assertions.assertThat(versions).isNotEmpty();
    assertThat(versions.get(0).getNumber()).isEqualTo("2.11.1-rc1");
    assertThat(versions.get(0).getDate()).isEqualTo("2020-12-07T00:00:00.00Z");
    assertThat(versions.get(1).getNumber()).isEqualTo("2.11.0");
    assertThat(versions.get(1).getDate()).isEqualTo("2020-12-04T00:00:00.00Z");
  }

  @Test
  void shouldParseChangeTypes() {
    Changelog changelog = new ChangelogParser().parse(CHANGELOG);
    List<Changelog.Version> versions = changelog.getVersions();

    Assertions.assertThat(versions).isNotEmpty();
    Map<String, List<Changelog.Change>> changes = versions.get(1).getChanges();

    assertThat(changes).containsKeys("Added", "Changed", "Fixed");
  }

  @Test
  void shouldParseChanges() {
    Changelog changelog = new ChangelogParser().parse(CHANGELOG);
    List<Changelog.Version> versions = changelog.getVersions();

    Assertions.assertThat(versions).isNotEmpty();
    Map<String, List<Changelog.Change>> changes = versions.get(1).getChanges();

    Assertions.assertThat(changes.get("Added")).hasSize(8);
    assertThat(changes.get("Added").get(0).getValue()).isEqualTo("\"Add\" tooltips to short links on repository overview ([#1441](https://github.com/scm-manager/scm-manager/pull/1441))");
    assertThat(changes.get("Added").get(1).getValue()).isEqualTo("Show the date of the last commit for branches in the frontend ([#1439](https://github.com/scm-manager/scm-manager/pull/1439))");
  }

  @Test
  void shouldParseLinks() {
    Changelog changelog = new ChangelogParser().parse(CHANGELOG_WITH_LINKS);
    List<Changelog.VersionLink> links = changelog.getLinks();

    Assertions.assertThat(links).hasSize(2);

    assertThat(links.get(0).getVersion()).isEqualTo("2.11.0");
    assertThat(links.get(0).getLink()).isEqualTo("https://www.scm-manager.org/download/2.11.0");
  }
}
