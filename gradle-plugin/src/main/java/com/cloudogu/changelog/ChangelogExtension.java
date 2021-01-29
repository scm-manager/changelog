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
