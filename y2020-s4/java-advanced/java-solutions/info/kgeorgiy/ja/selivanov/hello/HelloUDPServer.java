package info.kgeorgiy.ja.selivanov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static info.kgeorgiy.ja.selivanov.hello.Common.*;

public class HelloUDPServer implements HelloServer {

    private boolean launched = false;
    private ExecutorService threadPool;
    private DatagramSocket socket;

    // :NOTE: утечка ресурсов при повторных вызовах
    @Override
    public void start(int port, int threads) {
        if (launched) {
            return;
        }
        launched = true;
        try {
            threadPool = Executors.newFixedThreadPool(threads);
            socket = new DatagramSocket(port);

            int size = socket.getSendBufferSize();
            for (int i = 0; i < threads; i++) {
                threadPool.submit(() -> {
                    DatagramPacket packet = new DatagramPacket(new byte[size], size);
                    while (!socket.isClosed() && !Thread.interrupted()) {
                        try {
                            socket.receive(packet);
                            // :NOTE: Несмотря на то, что текущий способ получения ответа по запросу очень прост,
                            // сервер должен быть рассчитан на ситуацию,
                            // когда этот процесс может требовать много ресурсов и времени.
                            packet.setData(bytes("Hello, " + decode(packet)));
                            socket.send(packet);
                        } catch (IOException ignored) {
                        }
                    }
                });
            }
        } catch (SocketException e) {
            System.err.println("Bad socket");
        }
    }

    @Override
    public void close() {
        socket.close();
        closePool(threadPool, 1);
        launched = false;
    }

    public static void main(String... args) {
        if (wrongArgs(args, 2)) {
            return;
        }
        Function<Integer, Integer> intArg = i -> Integer.parseInt(args[i]);
        try (HelloServer server = new HelloUDPServer()) {
            server.start(intArg.apply(0), intArg.apply(1));
        } catch (NumberFormatException e) {
            System.err.println("Cannot parse number: " + e.getMessage());
        }
    }
}
