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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cloudogu.scm.changelog.Changelog.DATE_FORMAT;

public class ChangelogParser {

  private static final Pattern VERSION_PATTERN = Pattern.compile("##\\s+\\[?([0-9.]+(?:-[^]\\s]*)?)]? - (....-..-..)");
  private static final Pattern TYPE_PATTERN = Pattern.compile("###\\s+(.*)");
  private static final Pattern CHANGE_PATTERN = Pattern.compile("-\\s+(.*)");
  private static final Pattern LINK_PATTERN = Pattern.compile("\\[([0-9.]+(?:-.*)?)]:\\s+(.+)");

  public Changelog parse(Path changelogFile) {
    List<String> lines = readChangelogFile(changelogFile);
    return parse(lines);
  }

  Changelog parse(List<String> lines) {
    LineParser lineParser = new LineParser();
    lines.stream().map(String::trim).forEach(lineParser::readLine);
    return lineParser.get();
  }

  private List<String> readChangelogFile(Path changelogFile) {
    try {
      return Files.readAllLines(changelogFile);
    } catch (IOException e) {
      throw new ReadChangelogFileException(changelogFile, e);
    }
  }

  private class LineParser {
    private boolean headerFinished = false;

    private final List<String> header = new ArrayList<>();
    private final List<Changelog.Version> versions = new ArrayList<>();
    private final List<Changelog.VersionLink> links = new ArrayList<>();

    private Map<String, List<Changelog.Change>> changeTypes;
    private List<Changelog.Change> changes;
    private String currentVersion;
    private Instant currentDate;
    private String currentType;

    void readLine(String line) {
      Matcher versionMatcher = VERSION_PATTERN.matcher(line);
      Matcher typeMatcher = TYPE_PATTERN.matcher(line);
      Matcher changeMatcher = CHANGE_PATTERN.matcher(line);
      Matcher linkMatcher = LINK_PATTERN.matcher(line);
      if (versionMatcher.matches()) {
        finishCurrentVersion();
        currentVersion = versionMatcher.group(1);
        currentDate = LocalDate.parse(versionMatcher.group(2), DATE_FORMAT).atStartOfDay(ZoneOffset.UTC).toInstant();
        headerFinished = true;
      } else if (typeMatcher.matches()) {
        finishCurrentType();
        currentType = typeMatcher.group(1);
      } else if (!headerFinished) {
        header.add(line);
      } else if (changeMatcher.matches()) {
        if (changes == null) {
          changes = new ArrayList<>();
        }
        changes.add(new Changelog.Change(changeMatcher.group(1)));
      } else if (linkMatcher.matches()) {
        links.add(new Changelog.VersionLink(linkMatcher.group(1), linkMatcher.group(2)));
      }
    }

    Changelog get() {
      finishCurrentVersion();
      return new Changelog(header, versions, links);
    }

    private void finishCurrentVersion() {
      finishCurrentType();
      if (currentVersion != null) {
        Changelog.Version version = new Changelog.Version(currentVersion, currentDate, changeTypes);
        versions.add(version);
      }
      changeTypes = null;
      currentVersion = null;
    }

    private void finishCurrentType() {
      if (currentType != null) {
        if (changeTypes == null) {
          changeTypes = new LinkedHashMap<>();
        }
        changeTypes.put(currentType, changes);
      }
      changes = null;
      currentType = null;
    }
  }
}
