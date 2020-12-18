package cloudogu.scm;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import cloudogu.scm.changelog.lib.ChangelogUpdater;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 *
 * @phase process-sources
 */
@Mojo(name = "update-changelog")
public class ChangelogMojo
    extends AbstractMojo
{
    @Parameter(property = "changelog.version", required = true)
    private String version;

    @Parameter(property = "changelog.file", defaultValue = "CHANGELOG.md")
    private String changelogFile;

    @Parameter(property = "changelog.unreleased", defaultValue = "unreleased")
    private String unreleasedDirectory;

    @Parameter(property = "changelog.downloadUrlPattern")
    private String downloadUrlPattern;

    public void execute()
        throws MojoExecutionException
    {
        try
        {
            ChangelogUpdater changelogUpdater = new ChangelogUpdater(Paths.get(changelogFile), Paths.get(unreleasedDirectory), version);
            if (downloadUrlPattern != null) {
                changelogUpdater.withDownloadUrls(downloadUrlPattern);
            }
            changelogUpdater.update();
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Could not update changelog (" + changelogFile + ") with unreleased content (" + unreleasedDirectory + ") for version " + version, e );
        }
    }
}
