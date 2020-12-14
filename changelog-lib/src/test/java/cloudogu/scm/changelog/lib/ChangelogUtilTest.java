package cloudogu.scm.changelog.lib;

import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

public class ChangelogUtilTest {

    @Test
    public void shouldCreateCorrectChangelog() throws FileNotFoundException {
        final String changelog = ChangelogUtil.getChangelog(new File(Resources.getResource("test.yaml").getFile()));
        Assert.assertEquals("", changelog);
    }

}