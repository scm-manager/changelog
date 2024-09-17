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

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GradlePlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    ChangelogExtension extension = project.getExtensions().create("changelog", ChangelogExtension.class);
    project.getTasks().register(UpdateChangelogTask.NAME, UpdateChangelogTask.class).configure(task -> {
      task.getFile().set(extension.getFile());
      task.getDirectory().set(extension.getDirectory());
      task.getVersionUrlPattern().set(extension.getVersionUrlPattern());
    });
  }
}
