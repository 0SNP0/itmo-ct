package info.kgeorgiy.ja.selivanov.hello;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Common {

    static final long SELECTOR_TIMEOUT = 200;
    static final int BUFFER_CAPACITY = 2048;

    static SocketAddress socketAddress(String host, int port) throws UnknownHostException {
        return new InetSocketAddress(InetAddress.getByName(host), port);
    }
    static String decode(DatagramPacket packet) {
        return new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
    }

    static byte[] bytes(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    static void closePool(ExecutorService threadPool, long timeout) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(timeout, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
    }

    static String responsePattern(int thread, int request) {
        return String.format("[\\D]*%d[\\D]*%d[\\D]*", thread, request);
    }

    static boolean wrongArgs(String[] args, int expected) {
        if (args == null || args.length != expected || Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println(expected + " not-null arguments should be");
            return true;
        }
        return false;
    }
}
