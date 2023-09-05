package info.kgeorgiy.ja.selivanov.walk;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Objects;

public class WalkCommon {

    static void run(final String[] args, final WalkInterface walk) {
        if (args == null || args.length != 2 || Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("Expected 2 not-null arguments!");
            return;
        }

        try {
            final Path inPath = Paths.get(args[0]), outPath = Paths.get(args[1]);

            try (final BufferedReader reader = Files.newBufferedReader(inPath)) {
                try (final BufferedWriter writer = Files.newBufferedWriter(outPath)) {

                    // :NOTE: outPath.getParent() to a variable
                    if (outPath.getParent() != null) {
                        Files.createDirectories(outPath.getParent());
                    }
                    final WalkVisitor visitor = walk.visitor(writer);
                    String path;
                    while ((path = reader.readLine()) != null) {
                        try {
                            visitor.walk(path);
                        } catch (InvalidPathException e) {
                            visitor.writeDefault(path);
                        }
                    }

                } catch (IOException e) {
                    printError("Error writing to file", e);
                }
            } catch (IOException e) {
                printError("Input file error", e);
            }
        } catch (InvalidPathException e) {
            printError("Invalid path", e);
        }
    }

    private static void printError(final String message, final Exception e) {
        System.err.printf("%s: %s%n", message, e.getMessage());
    }
}

interface WalkInterface {
    default WalkVisitor visitor(final BufferedWriter writer) {
        return new WalkVisitor(writer);
    }
}