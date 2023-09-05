package common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public interface Utils {
    public static String[] split(String str) {
        String[] s = str.split("'");
        List<String> res = new ArrayList<>();
        for (int i = 0; i < s.length; i++) {
            if (i % 2 == 0) {
                res.addAll(List.of(s[i].split(" +")));
            } else {
                res.add(s[i]);
            }
        }
        return res.toArray(new String[0]);
    }

    public static String username() {
        return System.getProperty("user.name");
    }

    public static String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "[unknown_host]";
        }
    }
}
