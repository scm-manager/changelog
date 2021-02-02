<p align="center">
  <a href="https://www.scm-manager.org/">
    <img alt="SCM-Manager" src="https://download.scm-manager.org/images/logo/scm-manager_logo.png" width="500" />
  </a>
</p>
<h1 align="center">
  Changelog
</h1>

This repository contains a library and a [Gradle](https://gradle.org/) plugin to support the SCM-Manager changelog flow.

## SCM-Manager changelog flow

At SCM-Manager we log changes in the [keep a changelog](https://keepachangelog.com/en/1.0.0/) format at the root of 
our projects (e.g.: [SCM-Manager](https://github.com/scm-manager/scm-manager/blob/develop/CHANGELOG.md)).
This format is nice for the reader, because it contains all information for every release in a single file.


But it is hard to maintain, because one file for all changes often leads to merge conflicts.
We are working with git flow and using branches for each feature or bugfix.
Each of these branches changes the changelog to reflect the changes for the release.
If a feature is ready it will be merged into the develop branch and 
nearly every time this merge leads to a merge conflict.


To avoid these conflicts and error prone manual merges, we have searched for a solution for this problem.
Luckily we are not the only one with this problem.
The guys from [GitLab had encountered the same problem](https://about.gitlab.com/blog/2018/07/03/solving-gitlabs-changelog-conflict-crisis/).
So we decided to use the same flow.


Instead of changing a single markdown file, we are creating yaml files in a directory, one for each feature or bugfix e.g.:

```yaml
- type: added
  description: Awesome feature
- type: removed
  description: Not so awesome feature
```

When we prepare a release, we take all these changelog entry files and write them into the changelog file.
In order to support this approach, 
we build a gradle plugin which does the merge of the entry files with the changelog for us.

## Changelog entry files

The name of the files should reflect the changes in a few words separated by underscores.
In the most cases the name could be the name of the feature/bugfix branch without the type prefix e.g.:

* `new_changelog_process.yaml`
* `delete_branch_permissions.yaml`

A changelog entry file can consist of several changes.
Each change must have a type and a description.

The type must be one of the following types:

* `added` - for new features
* `changed` - for changes in existing functionality
* `deprecated` - for soon-to-be removed features
* `removed` - for now removed features
* `fixed` - for any bug fixes
* `security` - in case of vulnerabilities

The description should describe the change in one sentence.
It can be written in markdown and should contain links to issues and pull requests.

### Examples

```yaml
- type: changed
  description: Implement new changelog process ([#1517](https://github.com/scm-manager/scm-manager/issues/1517))
- type: fixed
  description: Fix permission check for branch deletion ([#1515](https://github.com/scm-manager/scm-manager/pull/1515))
```

## Gradle Plugin

### Installation

To install the plugin, just add the following snippet to your `build.gradle` file.

```groovy
plugins {
  id "org.scm-manager.changelog" version "..."
}
```

The version should always be the latest available.
The latest version can be found in the [Gradle Plugin Center](https://plugins.gradle.org/plugin/org.scm-manager.changelog).

### Configuration

```groovy
changelog {
  file = file('CHANGELOG.md')
  directory = file('gradle/changelog')
  versionUrlPattern = 'https://scm-manager.org/download/{0}'
}
```

The following table shows the available options.

| Name | Default | Description |
| ---- | -------- | ----------- |
| file | CHANGELOG.md | Path to the changelog file |
| dir | gradle/changelog | Path to the directory with the changelog entries |
| versionUrlPattern | null | Pattern to generate links for version entries ({0} will be replaced with the version) |

### Usage

To update the changelog, we have to run the `updateChangelog` task and specify the version of the release e.g.:

```bash
./gradlew updateChangelog --release=1.0.1
```

## Need help?

Looking for more guidance? Full documentation lives on our [homepage](https://www.scm-manager.org/docs/) or the dedicated pages for our [plugins](https://www.scm-manager.org/plugins/). Do you have further ideas or need support?

- **Community Support** - Contact the SCM-Manager support team for questions about SCM-Manager, to report bugs or to request features through the official channels. [Find more about this here](https://www.scm-manager.org/support/).

- **Enterprise Support** - Do you require support with the integration of SCM-Manager into your processes, with the customization of the tool or simply a service level agreement (SLA)? **Contact our development partner Cloudogu! Their team is looking forward to discussing your individual requirements with you and will be more than happy to give you a quote.** [Request Enterprise Support](https://cloudogu.com/en/scm-manager-enterprise/).
