/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.netty;

import com.tsixi.mars.net.Connect;
import com.tsixi.mars.net.NetAddress;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.net.InetSocketAddress;

/**
 * Created on 2017/4/5.
 *
 * @author Alan
 * @since 1.0
 */
public class ConnectImpl implements Connect {

    private Channel channel;

    public ConnectImpl(Channel channel) {
        this.channel = channel;
    }

    @Override
    public boolean write(Object msg) {
        return channel.writeAndFlush(msg).isSuccess();
    }

    @Override
    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(
                ChannelFutureListener.CLOSE).isSuccess();
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public NetAddress address() {
        InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
        return new NetAddress(address.getAddress().getHostAddress(), address.getPort());
    }

    @Override
    public void onClose() {

    }

    @Override
    public void onCreate() {

    }
}
