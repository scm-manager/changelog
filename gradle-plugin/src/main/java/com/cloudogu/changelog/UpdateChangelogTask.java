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
