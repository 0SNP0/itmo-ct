package info.kgeorgiy.ja.selivanov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.function.Function;

import static info.kgeorgiy.ja.selivanov.hello.Common.*;

public class HelloUDPNonblockingClient implements HelloClient {

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        try (Selector selector = Selector.open()) {
            SocketAddress address = socketAddress(host, port);
            for (int i = 0; i < threads; i++) {
                try {
                    DatagramChannel channel = DatagramChannel.open();
                    channel.configureBlocking(false);
                    channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                    channel.connect(address);
                    channel.register(selector, SelectionKey.OP_WRITE, new int[]{i, 0});
                } catch (IOException e) {
                    return;
                }
            }
            while (!Thread.interrupted() && !selector.keys().isEmpty()) {
                try {
                    selector.select(SELECTOR_TIMEOUT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (selector.selectedKeys().isEmpty()) {
                    for (SelectionKey key : selector.keys()) {
                        send(key, address, prefix);
                    }
                } else {
                    for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext(); ) {
                        SelectionKey key = it.next();
                        if (key.isWritable()) {
                            key.interestOps(SelectionKey.OP_WRITE);
                            send(key, address, prefix);
                        } else if (key.isReadable()) {
                            ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_CAPACITY);
                            byteBuffer.clear();
                            try {
                                ((DatagramChannel) key.channel()).receive(byteBuffer);
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }
                            String response = new String(byteBuffer.array(), StandardCharsets.UTF_8);
                            int[] attach = (int[]) key.attachment();
                            if (response.matches(responsePattern(attach[0], attach[1]))) {
                                if (attach[1] + 1 < requests) {
                                    key.attach(new int[]{attach[0], attach[1] + 1});
                                    key.interestOps(SelectionKey.OP_WRITE);
                                } else {
                                    try {
                                        key.channel().close();
                                    } catch (IOException e) {
                                        System.out.println(e.getMessage());
                                    }
                                }
                                break;
                            } else {
                                send(key, address, prefix);
                            }
                        }
                        it.remove();
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Bad hostname");
        } catch (IOException e) {
            System.err.printf("I/O exception: %s%n", e.getMessage());
        }
    }

    private static void send(SelectionKey key, SocketAddress address, String prefix) {
        int[] attach = (int[]) key.attachment();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
        buffer.clear();
        buffer.put(bytes(String.format("%s%d_%d", prefix, attach[0], attach[1])));
        buffer.flip();
        try {
            ((DatagramChannel) key.channel()).send(buffer, address);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        key.interestOps(SelectionKey.OP_READ);
    }

    public static void main(String... args) {
        if (wrongArgs(args, 5)) {
            return;
        }
        Function<Integer, Integer> intArg = i -> Integer.parseInt(args[i]);
        try {
            new HelloUDPNonblockingClient().run(args[0], intArg.apply(1), args[2], intArg.apply(3), intArg.apply(4));
        } catch (NumberFormatException e) {
            System.err.println("Cannot parse number: " + e.getMessage());
        }
    }
}
