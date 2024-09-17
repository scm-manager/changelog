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

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

class ChangeEntries {

  public static final String ADDED = "Added";
  public static final String FIXED = "Fixed";
  public static final String CHANGED = "Changed";

  private static final Yaml YAML = new Yaml(new CustomClassLoaderConstructor(ChangelogEntry.class.getClassLoader()));
  private static final List<String> TYPE_ORDER = asList(ADDED, FIXED, CHANGED);

  Map<String, List<Changelog.Change>> from(Path path) {
    List<ChangelogEntry> entries = getEntries(path);
    Map<String, List<Changelog.Change>> changes = new LinkedHashMap<>();
    entries.forEach(
      entry -> changes
        .computeIfAbsent(entry.getType(), x -> new ArrayList<>())
        .add(new Changelog.Change(entry.getDescription()))
    );
    return orderByChangeTypes(changes);
  }

  private List<ChangelogEntry> getEntries(Path fileOrDirectory) {
    List<ChangelogEntry> entries = new ArrayList<>();
    if (Files.isDirectory(fileOrDirectory)) {
      try (Stream<Path> files = Files.list(fileOrDirectory)) {
        files.forEach(file -> entries.addAll(getEntries(file)));
      } catch (IOException e) {
        throw new ChangelogException("Failed to read changelog entries", e);
      }
    } else {
      entries.addAll(
        parse(fileOrDirectory)
          .stream()
          .map(it -> new ChangelogEntry(it.get("type"), it.get("description")))
          .collect(Collectors.toList())
      );
    }
    return entries;
  }

  private Map<String, List<Changelog.Change>> orderByChangeTypes(Map<String, List<Changelog.Change>> changeTypes) {
    Map<String, List<Changelog.Change>> orderedChange = new LinkedHashMap<>();
    TYPE_ORDER.forEach(
      type -> {
        if (changeTypes.containsKey(type)) {
          orderedChange.put(type, changeTypes.get(type));
        }
      }
    );
    changeTypes.forEach((key, value) -> {
      if (!TYPE_ORDER.contains(key)) {
        orderedChange.put(key, value);
      }
    });
    return orderedChange;
  }


  private static Collection<LinkedHashMap<String, String>> parse(Path fileOrDirectory) {
    try {
      return YAML.loadAs(Files.newInputStream(fileOrDirectory), List.class);
    } catch (IOException e) {
      throw new ChangelogException("failed to parse changelog entry", e);
    }
  }

  static class ChangelogEntry {
    private final String type;
    private final String description;

    public ChangelogEntry(String type, String description) {
      this.type = normalize(type);
      this.description = description;
    }

    private String normalize(String type) {
      return type.substring(0, 1).toUpperCase(Locale.ENGLISH)
        + type.toLowerCase(Locale.ENGLISH).substring(1);
    }

    public String getType() {
      return type;
    }

    public String getDescription() {
      return description;
    }
  }
}
