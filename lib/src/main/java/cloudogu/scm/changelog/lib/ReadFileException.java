package cloudogu.scm.changelog.lib;

import java.nio.file.Path;

class ReadFileException extends RuntimeException {
    public ReadFileException(Path path, Throwable cause) {
        super("could not read changelog file " + path, cause);
    }
}
