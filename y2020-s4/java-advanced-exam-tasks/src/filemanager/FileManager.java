package filemanager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import common.Fmt;
import common.Utils;

//a part of 'javashell'
import static shell.units.Command.c;

public class FileManager {
    private static final String GREETING = String.format(":: %s> ", Fmt.wrap("%s", Fmt.CYAN));
    private static final String HELP = "help", EXIT = "exit";
    private static final String DIR = "dir", RM = "rm", CD = "cd", CREATE = "create", MKDIR = "mkdir", RMDIR = "rmdir";

    private final Map<String, Supplier<Boolean>> info = Map.of(
            DIR, this::dir,
            // "ls", this::dir,
            HELP, FileManager::help);
    private final Map<String, Predicate<Path>> command = Map.of(
            RM, this::rm,
            CD, this::cd,
            CREATE, this::create,
            MKDIR, this::mkdir,
            RMDIR, this::rmdir);
    // private final Map<String, Predicate<String>> cmd =
    // command.keySet().stream().collect(Collectors.toMap(
    // x -> x, x -> (s -> command.get(x).test(Path.of(s)))));

    private Path currentDir;

    public FileManager() {
        currentDir = Paths.get(".").toAbsolutePath().normalize();
    }

    private String path() {
        return currentDir.toString();
    }

    private boolean dir() {
        try (var stream = Files.newDirectoryStream(currentDir)) {
            stream.forEach(FileManager::printPath);
        } catch (IOException e) {
            System.out.println("Unable to read directory");
            return false;
        }
        return true;
    }

    private static void printPath(Path path) {
        if (Files.isDirectory(path)) {
            Fmt.println(" " + path.getFileName(), Fmt.CYAN + Fmt.BOLD);
        } else {
            System.out.printf(" %s%n", path.getFileName());
        }
    }

    private boolean cd(Path dir) {
        Path path = currentDir.resolve(dir).toAbsolutePath().normalize();
        if (Files.isDirectory(path)) {
            currentDir = path;
            return true;
        }
        System.out.println("Incorrect directory");
        return false;
    }

    private boolean create(Path file) {
        if (Files.exists(file)) {
            System.out.println("File or directory is already exists!");
            return false;
        }
        try {
            Files.createFile(file);
            System.out.println("Done!");
            return true;
        } catch (IOException e) {
            System.out.println("Error when creating file: " + e.getLocalizedMessage());
        }
        return false;
    }

    private boolean rm(Path file) {
        if (Files.isDirectory(file)) {
            System.out.printf("Use %s to delete a directory%n", Fmt.wrap(RMDIR, Fmt.GREEN));
            return false;
        }
        try {
            boolean res = Files.deleteIfExists(file);
            System.out.println(res ? "Done!" : "File is not exists!");
            return res;
        } catch (IOException e) {
            System.out.println("Error when deleting file: " + e.getLocalizedMessage());
        }
        return false;
    }

    private boolean mkdir(Path dir) {
        if (Files.exists(dir)) {
            System.out.println("File or directory is already exists!");
            return false;
        }
        try {
            Files.createDirectory(dir);
            return true;
        } catch (IOException e) {
            System.out.println("Error when creating directory: " + e.getLocalizedMessage());
        }
        return false;
    }

    private boolean rmdir(Path dir) {
        // TODO:
        return false;
    }

    private void run(String[] line) {
        if (!switch (line.length) {
            case 1 -> info.getOrDefault(line[0], FileManager::incorrectCommand).get();
            case 2 -> command.getOrDefault(line[0], FileManager::incorrectCommand).test(Path.of(line[1]));
            default -> incorrectCommand();
        } && !runInShell(line)) {
            Fmt.println("^Error", Fmt.RED);
        }
    }

    public static void main(String... args) {
        System.out.printf("Welcome! Type %s for help.%n", Fmt.wrap(HELP, Fmt.GREEN));
        FileManager manager = new FileManager();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            while (true) {
                System.out.printf(GREETING, manager.path());
                String line = reader.readLine();
                if (line == null || line.equals(EXIT)) {
                    break;
                }
                if (line.equals("")) {
                    continue;
                }
                manager.run(Utils.split(line));
            }
        } catch (IOException e) {
            System.err.println("Input error");
        }
        System.out.println("Exiting...");
    }

    private static boolean incorrectCommand(Path... args) {
        // System.out.println("Incorrect command or arguments number");
        return false;
    }

    private static boolean help() {
        System.out.printf(Fmt.wrap("""
                \s• %s — вывести оглавление текущего каталога
                \s• %s <имя файла> — удалить файл
                \s• %s <директория> — перейти в заданную директорию.
                \s• %s <имя файла> — создать файл с заданным именем
                \s• %s <директория> — создать директорию
                \s• %s <директория> — удалить директорию (рекурсивно)
                \s• %s — выход
                """, Fmt.YELLOW), DIR, RM, CD, CREATE, MKDIR, RMDIR, EXIT);
        return true;
    }

    //a part of 'javashell'
    private static boolean runInShell(String[] cmd) {
        return c(cmd).stdio().eval();
    }
}