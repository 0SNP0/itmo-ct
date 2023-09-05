package info.kgeorgiy.ja.selivanov.walk;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.security.*;
import java.util.HexFormat;

public class WalkVisitor {
    private static final int HASH_LEN = 40;
    private static final String DEFAULT_HASH = "0".repeat(HASH_LEN);
    private static final int BUFF_SIZE = 4096;
    private static final byte[] buff = new byte[BUFF_SIZE];
    private final BufferedWriter writer;

    public WalkVisitor(final BufferedWriter writer) {
        this.writer = writer;
    }

    public void walk(final String path) throws IOException {
        calcSHA1(Paths.get(path));
    }

    private void writeHash(final String hash, final String path) throws IOException {
        // :NOTE: %n
        writer.write(String.format("%s %s%s", hash, path, System.lineSeparator()));
    }

    public void writeDefault(final String path) throws IOException {
        writeHash(DEFAULT_HASH, path);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public void calcSHA1(final Path file) throws IOException {
        String hashString = DEFAULT_HASH;
        try (final var fileStream = Files.newInputStream(file)) {
            // :NOTE: digest to a field
            final var digest = MessageDigest.getInstance("SHA-1");
            final var digestStream = new DigestInputStream(fileStream, digest);
            while (digestStream.read(buff) >= 0) {
                // digest has already been updated
            }
            hashString = HexFormat.of().formatHex(digest.digest());
        } catch (IOException ignored) {
        } catch (NoSuchAlgorithmException e) {
            System.err.printf("SHA1 is not available: %s%n", e.getMessage());
        } finally {
            writeHash(hashString, file.toString());
        }
    }
}
