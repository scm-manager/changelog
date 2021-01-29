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

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

class ChangeEntries {

  private static final Yaml YAML = new Yaml(new CustomClassLoaderConstructor(ChangelogEntry.class.getClassLoader()));
  private static final List<String> TYPE_ORDER = asList("added", "fixed", "changed");

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
      this.type = type;
      this.description = description;
    }

    public String getType() {
      return type;
    }

    public String getDescription() {
      return description;
    }
  }
}
