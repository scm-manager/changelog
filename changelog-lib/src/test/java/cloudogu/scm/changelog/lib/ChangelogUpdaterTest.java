package cloudogu.scm.changelog.lib;

import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangelogUpdaterTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldCreateCorrectChangelog() throws IOException, ParseException {
        final Path changelogResourceFile = Paths.get(Resources.getResource("changelog.md").getFile());
        final Path expectedChangelogResourceFile = Paths.get(Resources.getResource("expected_changelog.md").getFile());
        final Path unreleasedChangelogEntriesResourceFile = Paths.get(Resources.getResource("multiple").getFile());

        final Path changelogFile = folder.getRoot().toPath().resolve("CHANGELOG.md");
        Files.copy(changelogResourceFile, changelogFile);

        new ChangelogUpdater(changelogFile, unreleasedChangelogEntriesResourceFile, "2.12.0", Instant.parse("2020-12-15T10:15:30.00Z"))
                .withDownloadUrls("https://www.scm-manager.org/download/{0}")
                .update();

        final String actualChangelog = Files.readString(changelogFile);
        final String expectedChangelog = Files.readString(expectedChangelogResourceFile);
        assertThat(actualChangelog).isEqualTo(expectedChangelog);
    }

}
