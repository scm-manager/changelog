package cloudogu.scm.changelog.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public final class FileUtils {

    private FileUtils() {}

    public static String readFile(File file) throws IOException {
        StringBuilder fileContents = new StringBuilder((int)file.length());

        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)))) {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine()).append(System.lineSeparator());
            }
            return fileContents.toString();
        }
    }

    public static void copy(File from, File to) throws IOException {
        try (PrintWriter out = new PrintWriter(to)) {
            out.print(FileUtils.readFile(from));
        }
    }
}
