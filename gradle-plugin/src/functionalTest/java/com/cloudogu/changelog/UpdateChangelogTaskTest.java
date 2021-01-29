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

package com.cloudogu.changelog;

import org.assertj.core.util.CanIgnoreReturnValue;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("java:S5960")
class UpdateChangelogTaskTest {

  private static final String TASK = "updateChangelog";

  @Test
  void shouldFailWithoutVersion(@TempDir Path directory) throws IOException {
    settings(directory).create();
    buildDotGradle(directory).create();

    BuildResult result = GradleRunner.create()
      .withProjectDir(directory.toFile())
      .withPluginClasspath()
      .withArguments(TASK)
      .buildAndFail();

    assertThat(result.getOutput())
      .contains("No value has been specified for property 'version'");
  }

  @Test
  void shouldUpdateChangelogWithConfiguration(@TempDir Path directory) throws IOException {
    settings(directory).create();
    buildDotGradle(directory).content(
      "changelog {",
      "  file = file('logOfChanges.md')",
      "  directory = file('changes')",
      "  versionUrlPattern = 'https://scm-manager.org/download/{0}'",
      "}"
    ).create().read();
    file(directory, "changes", "delete-all.yml").content(
      "- type: Removed",
      "  description: Everything"
    ).create();
    File changelog = changelog(directory, "logOfChanges.md").content(
      "## [2.0.0] - 2020-12-07",
      "### added",
      "- All",
      "",
      "[2.0.0]: https://scm-manager.org/download/2.0.0"
    ).create();

    GradleRunner.create()
      .withProjectDir(directory.toFile())
      .withPluginClasspath()
      .withArguments(TASK, "--version=3.0.0")
      .build();

    String content = changelog.read();
    assertThat(content)
      .contains("## [3.0.0]")
      .contains("### Removed")
      .contains("- Everything")
      .contains("## [2.0.0]")
      .contains("### Added")
      .contains("- All")
      .contains("[3.0.0]: https://scm-manager.org/download/3.0.0")
      .contains("[2.0.0]: https://scm-manager.org/download/2.0.0");
  }

  @Test
  void shouldUpdateChangelogWithDefaults(@TempDir Path directory) throws IOException {
    settings(directory).create();
    buildDotGradle(directory).create();
    file(directory, "gradle", "changelog", "001.yml").content(
      "- type: changed",
      "  description: Feature a"
    ).create();
    file(directory, "gradle", "changelog", "002.yml").content(
      "- type: fixed",
      "  description: Feature b"
    ).create();
    File changelog = changelog(directory, "CHANGELOG.md").content(
      "## 1.0.0 - 2020-12-07",
      "### Added",
      "- Awesome feature"
    ).create();

    GradleRunner.create()
      .withProjectDir(directory.toFile())
      .withPluginClasspath()
      .withArguments(TASK, "--version=1.0.1")
      .build();

    String content = changelog.read();
    assertThat(content)
      .contains("## 1.0.1")
      .contains("### Fixed")
      .contains("- Feature b")
      .contains("### Changed")
      .contains("- Feature b");
  }

  private File settings(Path directory) {
    return file(directory, "settings.gradle").content("rootProject.name = 'chango'");
  }

  private File buildDotGradle(Path directory) {
    return file(directory, "build.gradle").content(
      "plugins {",
      "  id('org.scm-manager.changelog')",
      "}"
    );
  }

  private File changelog(Path directory, String... path) {
    return file(directory, path).content(
      "# Changelog",
      "All notable changes to this project will be documented in this file.",
      "",
      "The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),",
      "and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).",
      ""
    );
  }


  private File file(Path root, String... other) {
    Path path = root;
    for (String o : other) {
      path = path.resolve(o);
    }
    return new File(path);
  }

  private static class File {

    private final Path path;
    private final List<String> content = new ArrayList<>();

    private File(Path path) {
      this.path = path;
    }

    File content(String... lines) {
      content(Arrays.asList(lines));
      return this;
    }

    File content(Collection<String> lines) {
      content.addAll(lines);
      return this;
    }

    @CanIgnoreReturnValue
    File create() throws IOException {
      Files.createDirectories(path.getParent());
      if (content.isEmpty()) {
        Files.createFile(path);
      } else {
        Files.write(path, content, StandardCharsets.UTF_8);
      }
      return this;
    }

    String read() throws IOException {
      return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
  }

}
