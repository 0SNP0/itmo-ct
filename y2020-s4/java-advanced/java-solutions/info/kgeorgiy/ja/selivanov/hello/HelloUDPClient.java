package info.kgeorgiy.ja.selivanov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static info.kgeorgiy.ja.selivanov.hello.Common.*;

public class HelloUDPClient implements HelloClient {

    private static final int SO_TIMEOUT_MS = 100;

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        try {
            SocketAddress address = socketAddress(host, port);
            ExecutorService threadPool = Executors.newFixedThreadPool(threads);
            for (int i = 0; i < threads; i++) {
                int thread = i;
                threadPool.submit(() -> {
                    try (DatagramSocket socket = new DatagramSocket()) {
                        socket.setSoTimeout(SO_TIMEOUT_MS);

                        int size = socket.getReceiveBufferSize();
                        DatagramPacket request = new DatagramPacket(new byte[0], 0, address);
                        DatagramPacket response = new DatagramPacket(new byte[size], size);
                        for (int j = 0; j < requests; j++) {
                            String reqStr = String.format("%s%d_%d", prefix, thread, j);
                            byte[] reqData = bytes(reqStr);
                            while (!socket.isClosed() && !Thread.interrupted()) {
                                try {
                                    request.setData(reqData);
                                    socket.send(request);
                                    socket.receive(response);
                                    String respStr = decode(response);
                                    if (respStr.matches(responsePattern(thread, j))) {
                                        System.out.printf("New answer:%nRequest: %s%nResponse: %s%n", reqStr, respStr);
                                        break;
                                    }
                                } catch (IOException ignored) {
                                }
                            }
                        }
                    } catch (SocketException e) {
                        System.err.println("Bad socket");
                    }
                });
            }
            closePool(threadPool, 5L * threads * requests);
        } catch (UnknownHostException e) {
            System.err.println("Bad hostname");
        }
    }

    public static void main(String... args) {
        if (wrongArgs(args, 5)) {
            return;
        }
        Function<Integer, Integer> intArg = i -> Integer.parseInt(args[i]);
        try {
            new HelloUDPClient().run(args[0], intArg.apply(1), args[2], intArg.apply(3), intArg.apply(4));
        } catch (NumberFormatException e) {
            System.err.println("Cannot parse number: " + e.getMessage());
        }
    }
}
