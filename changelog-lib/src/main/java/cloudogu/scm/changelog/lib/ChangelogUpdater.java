package cloudogu.scm.changelog.lib;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ChangelogUpdater {

    private static final Yaml yaml = new Yaml(new CustomClassLoaderConstructor(ChangelogEntryDao.class.getClassLoader()));
    private final Path changelogFile;
    private final Path changelogsDirectory;
    private final String version;
    private final Instant date;

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
            newVersion.write(out);
            oldChangelog.getVersions().forEach(v -> v.write(out));
            oldChangelog.getLinks().forEach(link -> link.write(out));
            new Changelog.VersionLink(version, "https://www.scm-manager.org/download/" + version).write(out);
        }
    }

    static List<String> entriesToChangelog(List<Map.Entry<String, List<ChangelogEntryDao>>> entries) {
        List<String> newLines = new ArrayList<>();
        for (Map.Entry<String, List<ChangelogEntryDao>> entry : entries) {
            newLines.add(typeToString(entry.getKey()));
            entry.getValue().forEach(it -> newLines.add(entryToString(it)));
            newLines.add("");
        }
        return newLines;
    }

    static List<ChangelogEntryDao> getEntries(Path fileOrDirectory) {
        List<ChangelogEntryDao> entries = new ArrayList<>();
        try {
            if (Files.isDirectory(fileOrDirectory)) {
                Files.list(fileOrDirectory).forEach(file -> entries.addAll(getEntries(file)));
            } else {
                entries.addAll(
                        (Collection<? extends ChangelogEntryDao>) yaml.loadAs(Files.newInputStream(fileOrDirectory), List.class)
                                .stream()
                                .map(it -> new ChangelogEntryDao(((LinkedHashMap<String, String>) it).get("type"), ((LinkedHashMap<String, String>) it).get("description")))
                                .collect(Collectors.toList())
                );
            }
        } catch (IOException e) {
            throw new ReadFileException(fileOrDirectory, e);
        }
        return entries;
    }

    static String typeToString(String type) {
        return "### " + capitalize(type);
    }

    static String entryToString(ChangelogEntryDao entry) {
        return "- " + entry.getDescription();
    }

    static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    static class ChangelogEntryDao {
        private final String type;
        private final String description;

        public ChangelogEntryDao(String type, String description) {
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
