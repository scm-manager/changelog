package cloudogu.scm.changelog.lib;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public final class ChangelogUpdater {

    private static final Yaml yaml = new Yaml(new CustomClassLoaderConstructor(ChangelogEntryDao.class.getClassLoader()));
    private final Path changelogFile;
    private final Path changelogsDirectory;
    private final String version;
    private final Instant date;
    private String downloadUrlPattern;

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
                new Changelog.VersionLink(version, MessageFormat.format(downloadUrlPattern, version)).write(out);
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
        return downloadUrlPattern != null;
    }

    public ChangelogUpdater withDownloadUrls(String downloadUrlPattern) {
        this.downloadUrlPattern = downloadUrlPattern;
        return this;
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
