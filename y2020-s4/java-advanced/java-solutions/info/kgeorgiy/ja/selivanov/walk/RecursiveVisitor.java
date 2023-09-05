package info.kgeorgiy.ja.selivanov.walk;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class RecursiveVisitor extends WalkVisitor implements FileVisitor<Path> {

    public RecursiveVisitor(final BufferedWriter writer) {
        super(writer);
    }

    @Override
    public void walk(final String path) throws IOException {
        Files.walkFileTree(Paths.get(path), this);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        calcSHA1(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        writeDefault(file.toString());
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        return FileVisitResult.CONTINUE;
    }
}
