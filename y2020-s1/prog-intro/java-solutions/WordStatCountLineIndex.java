import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordStatCountLineIndex {

    public static boolean wordChecker(int c) {
        return Character.isLetter((char) c) || Character.getType(c) == Character.DASH_PUNCTUATION || c == '\'';
    }

    public static void main(String[] args) {
        Map<String, Occurrences> entries = new HashMap<>();
        try (Scanner sc = new Scanner(new File(args[0]))) {
            for (int line = 0, inline = 0; sc.hasNext(WordStatCountLineIndex::wordChecker); inline++) {
                int lb = sc.countOfLineSep(WordStatCountLineIndex::wordChecker);
                if (lb > 0) {
                    line += lb;
                    inline = 0;
                }
                String word = sc.next(WordStatCountLineIndex::wordChecker).toLowerCase();
                entries.computeIfAbsent(word, key -> new Occurrences()).add(line + 1, inline + 1);
            }
        } catch (IOException e) {
            System.err.println("I/O exception: " + e.getMessage());
            return;
        }

        OccurrencesComparator comp = new OccurrencesComparator(entries);
        SortedMap<String, Occurrences> sorted = new TreeMap<>(comp);
        sorted.putAll(entries);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
            try {
                for (Map.Entry<String, Occurrences> entry : sorted.entrySet()) {
                    writer.write(String.format("%s %d", entry.getKey(), entry.getValue().length()));
                    for (int i = 0; i < entry.getValue().length(); i++) {
                        writer.write(String.format(" %d:%d", entry.getValue().get(i)[0], entry.getValue().get(i)[1]));
                    }
                    writer.write("\n");
                }
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            System.err.println("I/O exception: " + e.getMessage());
        }
    }
}

class OccurrencesComparator implements Comparator<String> {

    Map<String, Occurrences> map;

    public OccurrencesComparator(Map<String, Occurrences> base) {
        this.map = base;
    }

    public int compare(String a, String b) {
        return map.get(a).compareTo(map.get(b));
    }
}
