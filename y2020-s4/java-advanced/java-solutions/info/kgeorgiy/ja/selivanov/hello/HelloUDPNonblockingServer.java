package info.kgeorgiy.ja.selivanov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static info.kgeorgiy.ja.selivanov.hello.Common.*;

public class HelloUDPNonblockingServer implements HelloServer {

    private boolean launched = false;
    private ExecutorService threadPool;
    private Selector selector;
    private DatagramChannel channel;
    private Deque<Package> free;
    private Queue<Package> used;

    @Override
    public void start(int port, int threads) {
        launched = true;
        free = new ConcurrentLinkedDeque<>();
        used = new ConcurrentLinkedQueue<>();
        try {
            selector = Selector.open();
        } catch (IOException e) {
            System.err.printf("Error when selector opening: %s%n", e.getMessage());
        }
        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, false);
            channel.bind(new InetSocketAddress(port));
            channel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            System.err.printf("DatagramChannel error: %s%n", e.getMessage());
        }
        threadPool = Executors.newFixedThreadPool(threads + 1);

        for (int i = 0; i < threads; i++) {
            free.add(new Package());
        }
        threadPool.submit(() -> {
            while (launched && !Thread.interrupted() && !channel.socket().isClosed()) {
                try {
                    selector.select(SELECTOR_TIMEOUT);
                } catch (IOException e) {
                    System.err.printf("Selector error: %s%n", e.getMessage());
                }
                for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); launched && it.hasNext(); ) {
                    SelectionKey key = it.next();
                    if (key.isReadable()) {
                        try {
                            Package pkg = free.pop();
                            ByteBuffer buffer = pkg.buffer().clear();
                            if (free.isEmpty()) {
                                key.interestOpsAnd(~SelectionKey.OP_READ);
                                selector.wakeup();
                            }
                            DatagramChannel datagramChannel = (DatagramChannel) key.channel();
                            SocketAddress address = datagramChannel.receive(buffer);
                            threadPool.submit(() -> {
                                buffer.flip();
                                String request = "Hello, " + StandardCharsets.UTF_8.decode(buffer);
                                used.add(new Package(ByteBuffer.wrap(bytes(request)), address));
                                key.interestOpsOr(SelectionKey.OP_WRITE);
                                selector.wakeup();
                            });
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                        }
                    } else if (key.isWritable()) {
                        Package pkg = used.remove();
                        if (used.isEmpty()) {
                            key.interestOpsAnd(~SelectionKey.OP_WRITE);
                            selector.wakeup();
                        }
                        ByteBuffer buffer = pkg.buffer();
                        DatagramChannel datagramChannel = (DatagramChannel) key.channel();
                        try {
                            datagramChannel.send(buffer, pkg.address());
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                        }
                        buffer.clear().flip();
                        free.add(pkg);
                        key.interestOpsOr(SelectionKey.OP_READ);
                        selector.wakeup();
                    }
                    it.remove();
                }
            }
        });
    }

    @Override
    public void close() {
        launched = false;
        threadPool.shutdown();
        closePool(threadPool, 1);
        try {
            selector.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        try {
            channel.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private record Package(ByteBuffer buffer, SocketAddress address) {
        Package() {
            this(ByteBuffer.allocate(BUFFER_CAPACITY), null);
        }
    }

    public static void main(String... args) {
        if (wrongArgs(args, 2)) {
            return;
        }
        Function<Integer, Integer> intArg = i -> Integer.parseInt(args[i]);
        try (HelloServer server = new HelloUDPNonblockingServer()) {
            server.start(intArg.apply(0), intArg.apply(1));
        } catch (NumberFormatException e) {
            System.err.println("Cannot parse number: " + e.getMessage());
        }
    }
}
