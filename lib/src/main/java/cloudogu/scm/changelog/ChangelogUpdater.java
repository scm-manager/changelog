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

import com.google.common.base.Strings;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class ChangelogUpdater {

  private final Path changelogFile;
  private final Path changelogsDirectory;
  private final Instant date;
  private String versionUrlPattern;
  private String version;

  public ChangelogUpdater(Path changelogFile, Path changelogsDirectory) {
    this(changelogFile, changelogsDirectory, Instant.now());
  }

  public ChangelogUpdater(Path changelogFile, Path changelogsDirectory, Instant date) {
    this.changelogFile = changelogFile;
    this.changelogsDirectory = changelogsDirectory;
    this.date = date;
  }

  public void update() throws IOException {
    Map<String, List<Changelog.Change>> newEntries = new ChangeEntries().from(changelogsDirectory);
    if (newEntries.isEmpty()) {
      return;
    }
    Changelog oldChangelog = new ChangelogParser().parse(changelogFile);
    String nextVersionNumber = determineNextVersionNumber(newEntries, oldChangelog);
    Changelog.Version newVersion = new Changelog.Version(nextVersionNumber, date, newEntries);
    try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(changelogFile))) {
      oldChangelog.getHeader().forEach(out::println);
      writeVersion(newVersion, out);
      oldChangelog.getVersions().forEach(v -> writeVersion(v, out));
      oldChangelog.getLinks().forEach(link -> link.write(out));
      if (shouldWriteLinks()) {
        new Changelog.VersionLink(nextVersionNumber, MessageFormat.format(versionUrlPattern, nextVersionNumber)).write(out);
      }
    }
  }

  private String determineNextVersionNumber(Map<String, List<Changelog.Change>> newEntries, Changelog oldChangelog) {
    if (Strings.isNullOrEmpty(version)) {
      String nextVersionNumber = new VersionComputer().computeNextVersionNumber(newEntries, oldChangelog);
      System.out.println("Using next version " + nextVersionNumber);
      return nextVersionNumber;
    } else {
      return version;
    }
  }

  private void writeVersion(Changelog.Version v, PrintWriter out) {
    if (shouldWriteLinks()) {
      v.writeWithLink(out);
    } else {
      v.write(out);
    }
  }

  private boolean shouldWriteLinks() {
    return versionUrlPattern != null && !versionUrlPattern.trim().isEmpty();
  }

  public ChangelogUpdater withVersionUrls(String versionUrlPattern) {
    this.versionUrlPattern = versionUrlPattern;
    return this;
  }

  public ChangelogUpdater withVersion(String version) {
    this.version = version;
    return this;
  }
}
