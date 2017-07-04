/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.netty;

import com.tsixi.mars.net.Connect;
import com.tsixi.mars.net.NetAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * <p>
 * Nio 连接池
 * </p>
 * <p>
 * Created on 2017/3/27.
 *
 * @author Alan
 * @since 1.0
 */
public class ConnectPool<T extends NettyConnect> {

    public final static int POOL_SIZE = 5;

    private Logger log = LoggerFactory.getLogger(getClass());

    private NetAddress netAddress;

    private NetAddress localAddress;

    private ChannelInitializer initializer;

    private int poolSize;

    private Bootstrap bootstrap;

    private Connect[] connectList;

    private Random random = new Random();

    public ConnectPool(NetAddress netAddress, ChannelInitializer initializer) {
        this(netAddress, null, initializer, POOL_SIZE);
    }

    public ConnectPool(NetAddress netAddress, NetAddress localAddress, ChannelInitializer initializer) {
        this(netAddress, localAddress, initializer, POOL_SIZE);
    }

    public ConnectPool(NetAddress netAddress, NetAddress localAddress, ChannelInitializer initializer,
                       int poolSize) {
        this.netAddress = netAddress;
        this.initializer = initializer;
        this.poolSize = poolSize;
        this.localAddress = localAddress;
    }

    public ConnectPool init() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true)
                .handler(initializer);
        if (localAddress != null) {
            bootstrap.localAddress(localAddress.getHost(), localAddress.getPort());
        }
        bootstrap.remoteAddress(netAddress.getHost(), netAddress.getPort());
        connectList = new Connect[poolSize];
        return this;
    }

    public T getConnect() {
        int index = random.nextInt(connectList.length);
        T connect = (T) connectList[index];
        if (connect == null || !connect.isActive()) {
            connect = create();
            connectList[index] = connect;
        }
        return connect;
    }

    private T create() {
        try {
            ChannelFuture cf = bootstrap.connect().sync();
            T connect = (T) cf.channel().pipeline().get(NettyConnect.class);
            return connect;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
