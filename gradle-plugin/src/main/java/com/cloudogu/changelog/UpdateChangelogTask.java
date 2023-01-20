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

import cloudogu.scm.changelog.ChangelogUpdater;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;
import java.time.Instant;

public class UpdateChangelogTask extends DefaultTask {

  public static final String NAME = "updateChangelog";

  @SuppressWarnings("UnstableApiUsage")
  private final RegularFileProperty file = getProject().getObjects().fileProperty();
  @SuppressWarnings("UnstableApiUsage")
  private final DirectoryProperty directory = getProject().getObjects().directoryProperty();
  private final Property<String> versionUrlPattern = getProject().getObjects().property(String.class);
  private final Property<String> version = getProject().getObjects().property(String.class);

  @OutputFile
  public RegularFileProperty getFile() {
    return file;
  }

  @InputDirectory
  public DirectoryProperty getDirectory() {
    return directory;
  }

  @Input
  @Optional
  public Property<String> getVersionUrlPattern() {
    return versionUrlPattern;
  }

  @Input
  @Option(option = "release", description = "Version number of the release for changelog update")
  @Optional
  public Property<String> getVersion() {
    return version;
  }

  @TaskAction
  void updateChangelog() throws IOException {
    ChangelogUpdater updater = new ChangelogUpdater(
      file.getAsFile().get().toPath(),
      directory.get().getAsFile().toPath(),
      Instant.now()
    );
    if (version.isPresent()) {
      updater.withVersion(version.get());
    }
    if (versionUrlPattern.isPresent()) {
      updater.withVersionUrls(versionUrlPattern.get());
    }
    updater.update();
  }

}
