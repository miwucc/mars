package com.tsixi.mars.rpc.client;

import com.tsixi.mars.net.NetAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 连接管理器
 *
 * @author alan
 * @since 1.0
 */
public class ConnectManage {

    private static final Logger log = LoggerFactory.getLogger(ConnectManage.class);
    private volatile static ConnectManage connectManage;

    EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(65536));

    private CopyOnWriteArrayList<RpcConnect> connectedHandlers = new CopyOnWriteArrayList<>();
    private Map<NetAddress, RpcConnect> connectedServerNodes = new ConcurrentHashMap<>();
    // private Map<InetSocketAddress, Channel> connectedServerNodes = new
    // ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();
    protected long connectTimeoutMillis = 6000;
    private AtomicInteger roundRobin = new AtomicInteger(0);
    private volatile boolean isRuning = true;

    private ConnectManage() {
    }

    public static ConnectManage getInstance() {
        if (connectManage == null) {
            synchronized (ConnectManage.class) {
                if (connectManage == null) {
                    connectManage = new ConnectManage();
                }
            }
        }
        return connectManage;
    }

    public void updateConnectedServer(List<String> allServerAddress) {
        if (allServerAddress != null) {
            if (allServerAddress.size() > 0) { // Get available server node
                // update local serverNodes cache
                HashSet<NetAddress> newAllServerNodeSet = new HashSet<>();
                for (int i = 0; i < allServerAddress.size(); ++i) {
                    String[] array = allServerAddress.get(i).split(":");
                    if (array.length == 2) { // Should check IP and port
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);
                        final NetAddress remotePeer = new NetAddress(host, port);
                        newAllServerNodeSet.add(remotePeer);
                    }
                }

                // Add new server node
                for (final NetAddress serverNodeAddress : newAllServerNodeSet) {
                    if (!connectedServerNodes.keySet().contains(serverNodeAddress)) {
                        connectServerNode(serverNodeAddress);
                    }
                }

                // Close and remove invalid server nodes
                for (int i = 0; i < connectedHandlers.size(); ++i) {
                    RpcConnect connectedServerHandler = connectedHandlers.get(i);
                    NetAddress remotePeer = connectedServerHandler.address();
                    if (!newAllServerNodeSet.contains(remotePeer)) {
                        log.info("Remove invalid server node " + remotePeer);
                        RpcConnect handler = connectedServerNodes.get(remotePeer);
                        handler.close();
                        connectedServerNodes.remove(remotePeer);
                        connectedHandlers.remove(connectedServerHandler);
                    }
                }

            } else { // No available server node ( All server nodes are down )
                log.error("No available server node. All server nodes are down !!!");
                for (final RpcConnect connectedServerHandler : connectedHandlers) {
                    NetAddress remotePeer = connectedServerHandler.address();
                    RpcConnect handler = connectedServerNodes.get(remotePeer);
                    handler.close();
                    connectedServerNodes.remove(connectedServerHandler);
                }
                connectedHandlers.clear();
            }
        }
    }

    public void reconnect(final RpcConnect handler, final NetAddress remotePeer) {
        if (handler != null) {
            connectedHandlers.remove(handler);
            connectedServerNodes.remove(handler.address());
        }
        connectServerNode(remotePeer);
    }

    /**
     * 连接至指定节点
     *
     * @param remotePeer
     */
    private void connectServerNode(final NetAddress remotePeer) {
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Bootstrap b = new Bootstrap();
                b.group(eventLoopGroup).channel(NioSocketChannel.class).handler(new RpcClientInitializer());

                ChannelFuture channelFuture = b.connect(remotePeer.getHost(), remotePeer.getPort());
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            log.debug("Successfully connect to remote server. remote peer = " + remotePeer);
                            RpcConnect handler = channelFuture.channel().pipeline().get(RpcConnect.class);
                            addHandler(handler);
                        }
                    }
                });
            }
        });
    }

    private void addHandler(RpcConnect handler) {
        connectedHandlers.add(handler);
        NetAddress remoteAddress = handler.address();
        connectedServerNodes.put(remoteAddress, handler);
        signalAvailableHandler();
    }

    private void signalAvailableHandler() {
        lock.lock();
        try {
            connected.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private boolean waitingForHandler() throws InterruptedException {
        lock.lock();
        try {
            return connected.await(this.connectTimeoutMillis, TimeUnit.MILLISECONDS);
        } finally {
            lock.unlock();
        }
    }

    public RpcConnect chooseHandler() {
        CopyOnWriteArrayList<RpcConnect> handlers = (CopyOnWriteArrayList<RpcConnect>) this.connectedHandlers
                .clone();
        int size = handlers.size();
        while (isRuning && size <= 0) {
            try {
                boolean available = waitingForHandler();
                if (available) {
                    handlers = (CopyOnWriteArrayList<RpcConnect>) this.connectedHandlers.clone();
                    size = handlers.size();
                }
            } catch (InterruptedException e) {
                log.error("Waiting for available node is interrupted! ", e);
                throw new RuntimeException("Can't connect any servers!", e);
            }
        }
        int index = (roundRobin.getAndAdd(1) + size) % size;
        return handlers.get(index);
    }

    public void stop() {
        isRuning = false;
        for (int i = 0; i < connectedHandlers.size(); ++i) {
            RpcConnect connectedServerHandler = connectedHandlers.get(i);
            connectedServerHandler.close();
        }
        signalAvailableHandler();
        threadPoolExecutor.shutdown();
        eventLoopGroup.shutdownGracefully();
    }
}
