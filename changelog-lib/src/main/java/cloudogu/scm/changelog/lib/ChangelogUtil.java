package cloudogu.scm.changelog.lib;

import com.google.common.annotations.VisibleForTesting;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ChangelogUtil {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Yaml yaml = new Yaml(new CustomClassLoaderConstructor(ChangelogEntryDao.class.getClassLoader()));

    private ChangelogUtil() {
    }

    public static void updateChangelog(File changelogFile, File changelogsDirectory, String version) throws IOException {
        updateChangelog(changelogFile, changelogsDirectory, version, new Date());
    }

    @VisibleForTesting
    static void updateChangelog(File changelogFile, File changelogsDirectory, String version, Date date) throws IOException {
        final List<ChangelogEntryDao> entries = getEntries(changelogsDirectory);
        if (entries.isEmpty()) {
            return;
        }
        final Map<String, List<ChangelogEntryDao>> groupedEntries = entries.stream().collect(Collectors.groupingBy(ChangelogEntryDao::getType));
        final List<Map.Entry<String, List<ChangelogEntryDao>>> filteredEntries = groupedEntries.entrySet().stream().filter(e -> !e.getValue().isEmpty()).collect(Collectors.toList());
        final String oldChangelogContent = FileUtils.readFile(changelogFile);
        final String newChangelogEntry = entriesToChangelog(filteredEntries);
        final String versionHeader = "## [" + version + "] - " + DATE_FORMAT.format(date);
        final int newestEntryIndex = oldChangelogContent.indexOf("## [");
        final String newChangelogContent =
                oldChangelogContent.substring(0, newestEntryIndex)
                        + versionHeader
                        + System.lineSeparator()
                        + newChangelogEntry
                        + System.lineSeparator()
                        + oldChangelogContent.substring(newestEntryIndex)
                        + "[" + version + "]: https://www.scm-manager.org/download/" + version;

        try (PrintWriter out = new PrintWriter(changelogFile)) {
            out.print(newChangelogContent);
        }
    }

    static String entriesToChangelog(List<Map.Entry<String, List<ChangelogEntryDao>>> entries) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, List<ChangelogEntryDao>> entry : entries) {
            stringBuilder.append(typeToString(entry.getKey()));
            entry.getValue().forEach(it -> stringBuilder.append(entryToString(it)));
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    static List<ChangelogEntryDao> getEntries(File fileOrDirectory) throws FileNotFoundException {
        List<ChangelogEntryDao> entries = new ArrayList<>();
        if (fileOrDirectory.isDirectory()) {
            for (File file : fileOrDirectory.listFiles()) {
                entries.addAll(getEntries(file));
            }
        } else {
            entries.addAll(
                    (Collection<? extends ChangelogEntryDao>) yaml.loadAs(new FileInputStream(fileOrDirectory), List.class)
                            .stream()
                            .map(it -> new ChangelogEntryDao(((LinkedHashMap<String, String>) it).get("type"), ((LinkedHashMap<String, String>) it).get("description")))
                            .collect(Collectors.toList())
            );
        }
        return entries;
    }

    static String typeToString(String type) {
        return System.lineSeparator() + "### " + capitalize(type);
    }

    static String entryToString(ChangelogEntryDao entry) {
        return System.lineSeparator() + "- " + entry.getDescription();
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
