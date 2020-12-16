package cloudogu.scm.changelog.lib;

import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.swing.text.DateFormatter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ChangelogUtilTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldCreateCorrectChangelog() throws IOException, ParseException {
        final File changelogResourceFile = new File(Resources.getResource("changelog.md").getFile());
        final File expectedChangelogResourceFile = new File(Resources.getResource("expected_changelog.md").getFile());
        final File unreleasedChangelogEntriesResourceFile = new File(Resources.getResource("multiple").getFile());

        final File changelogFile = folder.newFile("CHANGELOG.md");
        FileUtils.copy(changelogResourceFile, changelogFile);

        ChangelogUtil.updateChangelog(changelogFile, unreleasedChangelogEntriesResourceFile, "2.12.0", ChangelogUtil.DATE_FORMAT.parse("2020-12-15"));

        final String actualChangelog = FileUtils.readFile(changelogFile);
        final String expectedChangelog = FileUtils.readFile(expectedChangelogResourceFile);
        Assert.assertEquals(expectedChangelog, actualChangelog);
    }

}