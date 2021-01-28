package cloudogu.scm.changelog.lib;

import java.io.PrintWriter;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        private final String change;

        public Change(String change) {
            this.change = change;
        }

        public String getChange() {
            return change;
        }

        public void write(PrintWriter out) {
            out.println("- " + change);
        }
    }

    public static class Version {
        private final String version;
        private final Instant date;
        private final Map<String, List<Change>> changes;

        public Version(String version, Instant date, Map<String, List<Change>> changes) {
            this.version = version;
            this.date = date;
            this.changes = changes;
        }

        public String getVersion() {
            return version;
        }

        public Instant getDate() {
            return date;
        }

        public Map<String, List<Change>> getChanges() {
            return Collections.unmodifiableMap(changes);
        }

        public void write(PrintWriter out) {
            out.println("## " + version + " - " + DATE_FORMAT.format(date));
            writeChanges(out);
        }

        public void writeWithLink(PrintWriter out) {
            out.println("## [" + version + "] - " + DATE_FORMAT.format(date));
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
