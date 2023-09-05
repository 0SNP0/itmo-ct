package info.kgeorgiy.ja.selivanov.walk;

import java.io.*;

public class RecursiveWalk implements WalkInterface {
    public static void main(final String... args) {
        WalkCommon.run(args, new RecursiveWalk());
    }

    @Override
    public WalkVisitor visitor(BufferedWriter writer) {
        return new RecursiveVisitor(writer);
    }
}
