# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 2.11.1 - 2020-12-07
### Fixed
- Initialization of new git repository with master set as default branch ([#1467](https://github.com/scm-manager/scm-manager/issues/1467) and [#1470](https://github.com/scm-manager/scm-manager/pull/1470))

## 2.11.0 - 2020-12-04

### Added
- Add tooltips to short links on repository overview ([#1441](https://github.com/scm-manager/scm-manager/pull/1441))
- Show the date of the last commit for branches in the frontend ([#1439](https://github.com/scm-manager/scm-manager/pull/1439))
- Unify and add description to key view across user settings ([#1440](https://github.com/scm-manager/scm-manager/pull/1440))
- Healthcheck for docker image ([#1428](https://github.com/scm-manager/scm-manager/issues/1428) and [#1454](https://github.com/scm-manager/scm-manager/issues/1454))
- Tags can now be added and deleted through the ui ([#1456](https://github.com/scm-manager/scm-manager/pull/1456))
- The ui now displays tag signatures ([#1456](https://github.com/scm-manager/scm-manager/pull/1456))
- Repository import via URL for git ([#1460](https://github.com/scm-manager/scm-manager/pull/1460))
- Repository import via URL for hg ([#1463](https://github.com/scm-manager/scm-manager/pull/1463))

### Changed
- Send mercurial hook callbacks over separate tcp socket instead of http ([#1416](https://github.com/scm-manager/scm-manager/pull/1416))

### Fixed
- Language detection of files with interpreter parameters e.g.: `#!/usr/bin/make -f` ([#1450](https://github.com/scm-manager/scm-manager/issues/1450))
- Unexpected mercurial server pool stop ([#1446](https://github.com/scm-manager/scm-manager/issues/1446) and [#1457](https://github.com/scm-manager/scm-manager/issues/1457))
