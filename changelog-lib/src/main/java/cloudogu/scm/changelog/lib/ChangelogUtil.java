package cloudogu.scm.changelog.lib;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Yaml yaml = new Yaml(new CustomClassLoaderConstructor(ChangelogEntryDao.class.getClassLoader()));

    private ChangelogUtil() {
    }

    public static String getChangelog(File fileOrDirectory) throws FileNotFoundException {
        final List<ChangelogEntryDao> entries = getEntries(fileOrDirectory);
        final Map<String, List<ChangelogEntryDao>> groupedEntries = entries.stream().collect(Collectors.groupingBy(ChangelogEntryDao::getType));
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, List<ChangelogEntryDao>> entry : groupedEntries.entrySet()) {
            stringBuilder.append(typeToString(entry.getKey()));
            entry.getValue().forEach(it -> stringBuilder.append(entryToString(it)));
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
        return "### " + type + " - " + DATE_FORMAT.format(new Date());
    }

    static String entryToString(ChangelogEntryDao entry) {
        return "- " + entry.description;
    }

    static class ChangelogEntryDao {
        private String type;
        private String description;

        public ChangelogEntryDao(String type, String description) {
            this.type = type;
            this.description = description;
        }

        public ChangelogEntryDao() {
        }

        public String getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

}
