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

import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public class ChangelogExtension {

  private final RegularFileProperty file;
  private final DirectoryProperty directory;
  private final Property<String> versionUrlPattern;

  @Inject
  @SuppressWarnings("UnstableApiUsage")
  public ChangelogExtension(ProjectLayout layout, ObjectFactory objectFactory) {
    Directory projectDirectory = layout.getProjectDirectory();
    this.file = objectFactory.fileProperty().convention(projectDirectory.file("CHANGELOG.md"));
    this.directory = objectFactory.directoryProperty().convention(projectDirectory.dir("gradle/changelog"));
    this.versionUrlPattern = objectFactory.property(String.class);
  }

  public DirectoryProperty getDirectory() {
    return directory;
  }

  public RegularFileProperty getFile() {
    return file;
  }

  public Property<String> getVersionUrlPattern() {
    return versionUrlPattern;
  }
}
