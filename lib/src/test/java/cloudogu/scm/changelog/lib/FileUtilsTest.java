package cloudogu.scm.changelog.lib;

import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class FileUtilsTest {

    @Test
    public void shouldReadResourceFile() throws IOException {
        final File changelogResourceFile = new File(Resources.getResource("changelog.md").getFile());
        final String fileContent = FileUtils.readFile(changelogResourceFile);
        Assert.assertNotEquals("", fileContent);
    }

}