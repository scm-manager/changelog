package cloudogu.scm.changelog.lib;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangelogParserTest {

    public static final List<String> CHANGELOG = asList(
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
    public void shouldParseHeader() {
        Changelog changelog = new ChangelogParser().parse(CHANGELOG);

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
    public void shouldParseVersionHeader() {
        Changelog changelog = new ChangelogParser().parse(CHANGELOG);
        List<Changelog.Version> versions = changelog.getVersions();
        assertThat(versions).isNotEmpty();
        assertThat(versions.get(0).getVersion()).isEqualTo("2.11.1-rc1");
        assertThat(versions.get(0).getDate()).isEqualTo("2020-12-07T00:00:00.00Z");
        assertThat(versions.get(1).getVersion()).isEqualTo("2.11.0");
        assertThat(versions.get(1).getDate()).isEqualTo("2020-12-04T00:00:00.00Z");
    }

    @Test
    public void shouldParseChangeTypes() {
        Changelog changelog = new ChangelogParser().parse(CHANGELOG);
        List<Changelog.Version> versions = changelog.getVersions();

        assertThat(versions).isNotEmpty();
        Map<String, List<Changelog.Change>> changes = versions.get(1).getChanges();

        assertThat(changes).containsKeys("Added", "Changed", "Fixed");
    }

    @Test
    public void shouldParseChanges() {
        Changelog changelog = new ChangelogParser().parse(CHANGELOG);
        List<Changelog.Version> versions = changelog.getVersions();

        assertThat(versions).isNotEmpty();
        Map<String, List<Changelog.Change>> changes = versions.get(1).getChanges();

        assertThat(changes.get("Added")).hasSize(8);
        assertThat(changes.get("Added").get(0).getChange()).isEqualTo("Add tooltips to short links on repository overview ([#1441](https://github.com/scm-manager/scm-manager/pull/1441))");
        assertThat(changes.get("Added").get(1).getChange()).isEqualTo("Show the date of the last commit for branches in the frontend ([#1439](https://github.com/scm-manager/scm-manager/pull/1439))");
    }

    @Test
    public void shouldParseLinks() {
        Changelog changelog = new ChangelogParser().parse(CHANGELOG);
        List<Changelog.VersionLink> links = changelog.getLinks();

        assertThat(links).hasSize(2);

        assertThat(links.get(0).getVersion()).isEqualTo("2.11.0");
        assertThat(links.get(0).getLink()).isEqualTo("https://www.scm-manager.org/download/2.11.0");
    }
}
