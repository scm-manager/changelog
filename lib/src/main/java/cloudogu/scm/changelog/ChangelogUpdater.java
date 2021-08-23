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
  private final String version;
  private final Instant date;
  private String versionUrlPattern;

  public ChangelogUpdater(Path changelogFile, Path changelogsDirectory, String version) {
    this(changelogFile, changelogsDirectory, version, Instant.now());
  }

  public ChangelogUpdater(Path changelogFile, Path changelogsDirectory, String version, Instant date) {
    this.changelogFile = changelogFile;
    this.changelogsDirectory = changelogsDirectory;
    this.version = version;
    this.date = date;
  }

  public void update() throws IOException {
    Map<String, List<Changelog.Change>> newEntries = new ChangeEntries().from(changelogsDirectory);
    if (newEntries.isEmpty()) {
      return;
    }
    Changelog.Version newVersion = new Changelog.Version(version, date, newEntries);
    Changelog oldChangelog = new ChangelogParser().parse(changelogFile);
    try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(changelogFile))) {
      oldChangelog.getHeader().forEach(out::println);
      writeVersion(newVersion, out);
      oldChangelog.getVersions().forEach(v -> writeVersion(v, out));
      oldChangelog.getLinks().forEach(link -> link.write(out));
      if (shouldWriteLinks()) {
        new Changelog.VersionLink(version, MessageFormat.format(versionUrlPattern, version)).write(out);
      }
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

}
