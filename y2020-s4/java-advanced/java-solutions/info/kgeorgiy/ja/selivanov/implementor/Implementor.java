package info.kgeorgiy.ja.selivanov.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import static info.kgeorgiy.ja.selivanov.implementor.CodeGen.*;

/**
 * Implementor class
 */
public class Implementor implements JarImpler {
    /**
     * Produces code implementing class or interface specified by provided {@code token}.
     * <p>
     * Generated class classes name should be same as classes name of the type token with {@code Impl} suffix
     * added. Generated source code should be placed in the correct subdirectory of the specified
     * {@code root} directory and have correct file name. For example, the implementation of the
     * interface {@link java.util.List} should go to {@code $root/java/util/ListImpl.java}
     *
     * @param token type token to create implementation for.
     * @param root  root directory.
     * @throws ImplerException when implementation cannot be generated.
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        checkToken(token);
        Path path = parentsPath(token, root);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            // :NOTE: выделить в отдельную функцию или добавить комментарий
            writer.write(CodeGen.generate(token).chars().mapToObj(x -> String.format("\\u%04X", x)).collect(Collectors.joining()));
        } catch (IOException e) {
            throw new ImplerException("Output file exception: " + e.getMessage());
        }
    }

    /**
     * Produces <var>.jar</var> file implementing class or interface specified by provided <var>token</var>.
     * <p>
     * Generated class classes name should be same as classes name of the type token with <var>Impl</var> suffix
     * added.
     *
     * @param token   type token to create implementation for.
     * @param jarFile target <var>.jar</var> file.
     * @throws ImplerException when implementation cannot be generated.
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        checkToken(token);
        // :NOTE: зачем?
        if (jarFile.getParent() == null) {
            return;
        }
        Path path;
        try {
            Files.createDirectories(jarFile.getParent());
            path = Files.createTempDirectory(jarFile.toAbsolutePath().getParent(), "tmp");
            try {
                implement(token, path);
                compileClass(token, path);
                generateManifest(token, path, jarFile);
            } finally {
                Files.walkFileTree(path, new Deleter());
            }
        } catch (IOException e) {
            throw new ImplerException("Token is not supported: " + token.getCanonicalName());
        }
    }

    /**
     * Checking the validity of the token
     * Token is valid if it isn't:
     * <ul>
     * <li>Primitive</li>
     * <li>Array</li>
     * <li>Enum</li>
     * <li>Private modifier</li>
     * <li>Final modifier</li>
     * </ul>
     *
     * @param token given token
     * @throws ImplerException if token is not valid
     */
    private void checkToken(Class<?> token) throws ImplerException {
        int modifiers = token.getModifiers();
        if (token.isPrimitive() || token.isArray() || token == Enum.class || Modifier.isPrivate(modifiers) || Modifier.isFinal(modifiers)) {
            throw new ImplerException("Token " + token.getCanonicalName() + " is not supported");
        }
    }

    /**
     * Creates a new path containing parents
     *
     * @param token the given token
     * @param path  the given root
     * @return path with root parents
     * @throws ImplerException if it failed to create parent directories
     */
    private Path parentsPath(Class<?> token, Path path) throws ImplerException {
        Path result = path.resolve(token.getPackageName().replace('.', File.separatorChar)).resolve(plusImpl(token) + ".java");
        Path parent = result.getParent();
        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new ImplerException("Exception when creating parent directories: " + e.getMessage());
            }
        }
        return result;
    }

    /**
     * Compiles class by given token via system compiler. {@link ToolProvider#getSystemJavaCompiler()}
     *
     * @param token the given token
     * @param path  the given path
     * @throws ImplerException if no java compiler provided, URI exception or compilation code isn't 0
     */
    private void compileClass(Class<?> token, Path path) throws ImplerException {
        final JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        if (javaCompiler == null) {
            throw new ImplerException("Missing java compiler");
        }
        try {
            Path classPath = Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path filePath = path.resolve(Path.of(token.getPackageName().replace('.', SEPARATOR_CHAR), plusImpl(token) + ".java"));
            List<String> args = List.of(filePath.toString(), "-cp", path + PATH_SEPARATOR + classPath);
            if (javaCompiler.run(null, null, null, args.toArray(new String[0])) != 0) {
                throw new ImplerException("Compilation error");
            }
        } catch (URISyntaxException e) {
            throw new ImplerException("URI exception: " + e.getMessage());
        }
    }

    /**
     * Generates manifest by the given token, temporary path, jar path
     *
     * @param token   the given token
     * @param path    temporary path
     * @param jarPath the jar path
     * @throws ImplerException if exception when writing jar output via {@link JarOutputStream}
     */
    private void generateManifest(Class<?> token, Path path, Path jarPath) throws ImplerException {
        Manifest manifest = new Manifest();
        Attributes mainAttributes = manifest.getMainAttributes();
        mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "0.1");
        try (JarOutputStream outStream = new JarOutputStream(Files.newOutputStream(jarPath), manifest)) {
            String className = token.getPackageName().replace('.', '/') + "/" + plusImpl(token) + ".class";
            outStream.putNextEntry(new ZipEntry(className));
            Files.copy(Paths.get(path.toString(), className), outStream);
        } catch (IOException e) {
            throw new ImplerException("Exception when writing jar output: " + e.getMessage());
        }
    }

    /**
     * {@link java.nio.file.FileVisitor} to delete a directory
     */
    private static class Deleter extends SimpleFileVisitor<Path> {
        /**
         * Deletes visited files
         *
         * @param file  {@link Path} to visit.
         * @param attrs {@link BasicFileAttributes} {@code file} attributes
         * @return {@link FileVisitResult#CONTINUE}
         * @throws IOException when the deletion failed
         */
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        /**
         * Deletes directory after visiting all files
         *
         * @param dir directory to visit
         * @param exc exception occurred during directory visiting
         * @return {@link FileVisitResult#CONTINUE}
         * @throws IOException when the deletion failed
         */
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    }

    /**
     * {@link Implementor} point of entry
     * <p>There should be 2 or 3 not null arguments
     * If there is three arguments and first argument is "-jar" then {@link JarImpler#implementJar(Class, Path)} runs
     *
     * @param args arguments
     */
    public static void main(String... args) {
        if (args == null || Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("args shouldn't contain null");
            return;
        }
        try {
            Implementor implementor = new Implementor();
            switch (args.length) {
                case 2:
                    implementor.implement(Class.forName(args[0]), Path.of(args[1]));
                    break;
                case 3:
                    if (args[0].equals("-jar")) {
                        implementor.implementJar(Class.forName(args[1]), Path.of(args[2]));
                        break;
                    }
                default:
                    System.err.println("Incorrect arguments for implementor");
            }
        } catch (ImplerException e) {
            System.err.println("Implementor exception - " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Incorrect class name: " + e.getMessage());
        } catch (InvalidPathException e) {
            System.err.println("Incorrect path: " + e.getMessage());
        }
    }
}
