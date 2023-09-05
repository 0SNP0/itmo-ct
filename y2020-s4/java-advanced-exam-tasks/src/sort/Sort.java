package sort;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;;

public class Sort {
    private static String NL = System.lineSeparator();

    private final Comparator<String> comp;
    private final Runtime runtime;
    private final long maxMemory;

    public Sort(Comparator<String> comp) {
        this.comp = comp;
        this.runtime = Runtime.getRuntime();
        this.maxMemory = runtime.freeMemory();
    }

    public Sort() {
        this(String::compareTo);
    }

    public void run(InputStream input) {
        List<Path> tmp = new ArrayList<>();
        List<String> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
                if (runtime.freeMemory() < maxMemory / 2) {
                    tmp.add(sortToTmp(list));
                }
            }
            if (!tmp.isEmpty()) {
                tmp.add(sortToTmp(list));
                merge(tmp);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        list.sort(comp);
        for (String line : list) {
            System.out.println(line);
        }
    }

    private Path sortToTmp(List<String> list) throws IOException {
        list.sort(comp);
        Path file = Files.createTempFile("extsort", "sortedtmpfile");
        try (BufferedWriter out = Files.newBufferedWriter(file, UTF_8)) {
            for (String line : list) {
                out.write(line);
                out.write(NL);
            }
        }
        list.clear();
        runtime.gc();
        return file;
    }

    private void merge(List<Path> tmp) throws IOException {
        List<Source> src = new ArrayList<>(tmp.size());
        for (Path file : tmp) {
            src.add(new Source(Files.newBufferedReader(file, UTF_8)));
        }
        while (!src.isEmpty()) {
            int ind = 0;
            String res = src.get(ind).get();
            for (int i = 1; i < src.size(); i++) {
                String str = src.get(i).get();
                if (str != null && (res == null || comp.compare(str, res) < 0)) {
                    ind = i;
                    res = str;
                }
            }
            if (res == null) break;
            System.out.println(res);
            src.get(ind).next();
        }
    }

    public static Sort config(SortType type, boolean ignore_leading_blanks, boolean ignore_case,
            boolean ignore_nonprinting, boolean reverse) {
        Comparator<String> comp = String::compareTo;
        if (ignore_leading_blanks) {
            comp = new CompImpl(comp, x -> {
                int i = 0;
                while (x.charAt(i) == ' ') {
                    i++;
                }
                return x.substring(i);
            });
        }
        if (ignore_case) {
            comp = new CompImpl(comp, String::toLowerCase);
        }
        if (ignore_nonprinting) {
            comp = new CompImpl(comp, x -> x.replaceAll("[\\p{C}]+", ""));
        }
        switch (type) {
            case DICTIONARY_ORDER -> {
                comp = new CompImpl(comp, x -> x.replaceAll("[^\\p{Z}\\p{L}\\p{N}]+", ""));
            }
            case GENERAL_NUMERIC_SORT -> {
            }
            case NUMERIC_SORT -> {
            }
            default -> {
            }
        }
        if (reverse) {
            comp = comp.reversed();
        }
        return new Sort(comp);
    }

    private static class CompImpl implements Comparator<String> {

        private final Comparator<String> comp;
        private final Function<String, String> conv;

        public CompImpl(Comparator<String> comp, Function<String, String> conv) {
            this.comp = comp;
            this.conv = conv;
        }

        @Override
        public int compare(String o1, String o2) {
            return comp.compare(conv.apply(o1), conv.apply(o2));
        }

    }

    public static void main(String... args) {
        InputStream input = null;
        boolean ignore_leading_blanks = false, ignore_case = false, ignore_nonprinting = false, reverse = false;
        SortType type = SortType.DEFAULT;
        for (String arg : args) {
            switch (arg) {
                case IGNORE_LEADING_BLANKS -> ignore_leading_blanks = true;
                case IGNORE_CASE -> ignore_case = true;
                case IGNORE_NONPRINTING -> ignore_nonprinting = true;
                case DICTIONARY_ORDER, GENERAL_NUMERIC_SORT, NUMERIC_SORT -> {
                    SortType newType = typeMap.get(arg);
                    if (type != SortType.DEFAULT && type != newType) {
                        System.err.println("Incompatible parameter: " + arg);
                        return;
                    }
                    type = newType;
                }
                case REVERSE -> reverse = true;
                case STDIN -> input = addInput(input, System.in);
                default -> {
                    Path path = Paths.get(arg);
                    try {
                        input = addInput(input, Files.newInputStream(path));
                    } catch (IOException e) {
                        System.err.println("Cannot read file: " + arg);
                        return;
                    }
                }
            }
        }
        Sort.config(type, ignore_leading_blanks, ignore_case, ignore_nonprinting, reverse).run(input == null ? System.in : input);
    }

    private static InputStream addInput(InputStream old, InputStream in) {
        return (old == null) ? in : new SequenceInputStream(old, in);
    }

    private static final String IGNORE_LEADING_BLANKS = "--ignore-leading-blanks", IGNORE_CASE = "--ignore-case",
            IGNORE_NONPRINTING = "--ignore-nonprinting", REVERSE = "--reverse", DICTIONARY_ORDER = "--dictionary-order",
            GENERAL_NUMERIC_SORT = "--general-numeric-sort", NUMERIC_SORT = "--numeric-sort", STDIN = "-";

    private static enum SortType {
        DEFAULT, DICTIONARY_ORDER, GENERAL_NUMERIC_SORT, NUMERIC_SORT
    }

    private static final Map<String, SortType> typeMap = Map.of(
            DICTIONARY_ORDER, SortType.DICTIONARY_ORDER,
            GENERAL_NUMERIC_SORT, SortType.GENERAL_NUMERIC_SORT,
            NUMERIC_SORT, SortType.GENERAL_NUMERIC_SORT);

    private static class Source implements AutoCloseable {
        private final BufferedReader reader;
        private String current;

        public Source(BufferedReader reader) throws IOException {
            this.reader = reader;
            this.current = reader.readLine();
        }

        public String get() {
            return current;
        }

        public void next() throws IOException {
            current = reader.readLine();
        }

        @Override
        public void close() throws Exception {
            reader.close();
        }
    }
}
