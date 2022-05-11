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

import java.io.PrintWriter;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;

public class Changelog {

  static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd").withZone(ZoneOffset.UTC);

  private final List<String> header;
  private final List<Version> versions;
  private final List<VersionLink> links;

  public Changelog(List<String> header, List<Version> versions, List<VersionLink> links) {
    this.header = header;
    this.versions = versions;
    this.links = links;
  }

  public List<String> getHeader() {
    return header;
  }

  public List<Version> getVersions() {
    return versions;
  }

  public List<VersionLink> getLinks() {
    return links;
  }

  public static class Change {
    private final String value;

    public Change(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public void write(PrintWriter out) {
      out.println("- " + value);
    }
  }

  public static class Version {
    private final String number;
    private final Instant date;
    private final Map<String, List<Change>> changes;

    public Version(String number, Instant date, Map<String, List<Change>> changes) {
      this.number = number;
      this.date = date;
      this.changes = changes == null ? emptyMap() : changes;
    }

    public String getNumber() {
      return number;
    }

    public Instant getDate() {
      return date;
    }

    public Map<String, List<Change>> getChanges() {
      return Collections.unmodifiableMap(changes);
    }

    public void write(PrintWriter out) {
      out.println("## " + number + " - " + DATE_FORMAT.format(date));
      writeChanges(out);
    }

    public void writeWithLink(PrintWriter out) {
      out.println("## [" + number + "] - " + DATE_FORMAT.format(date));
      writeChanges(out);
    }

    private void writeChanges(PrintWriter out) {
      getChanges().forEach((key, value) -> {
        out.println("### " + capitalize(key));
        value.forEach(change -> change.write(out));
        out.println();
      });
    }

    private String capitalize(String str) {
      return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
  }

  public static class VersionLink {
    private final String version;
    private final String link;

    public VersionLink(String version, String link) {
      this.version = version;
      this.link = link;
    }

    public String getVersion() {
      return version;
    }

    public String getLink() {
      return link;
    }

    public void write(PrintWriter out) {
      out.println("[" + version + "]: " + link);
    }
  }
}
