package cloudogu.scm.changelog.lib;

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

public class ChangeEntries {

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
                throw new RuntimeException(e);
            }
        } else {
            entries.addAll(
                    parse(fileOrDirectory)
                            .stream()
                            .map(it -> new ChangelogEntry(((LinkedHashMap<String, String>) it).get("type"), ((LinkedHashMap<String, String>) it).get("description")))
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
            return (Collection<LinkedHashMap<String, String>>) YAML.loadAs(Files.newInputStream(fileOrDirectory), List.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
