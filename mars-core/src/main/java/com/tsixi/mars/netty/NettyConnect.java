/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.netty;

import com.tsixi.mars.net.Connect;
import com.tsixi.mars.net.NetAddress;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * netty 连接抽象类
 * <p>
 * Created on 2017/3/31.
 *
 * @author Alan
 * @since 1.0
 */
public abstract class NettyConnect<T> extends SimpleChannelInboundHandler<T> implements Connect {

    protected Logger log = LoggerFactory.getLogger(getClass());
    protected Channel channel;
    protected NetAddress remoteAddress;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.debug("channel active,ctx={}", ctx);
        InetSocketAddress address = (InetSocketAddress) this.channel.remoteAddress();
        remoteAddress = new NetAddress(address.getAddress().getHostAddress(), address.getPort());
        onCreate();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        log.debug("client caught exception,ctx={}",ctx, cause);
    }

    @Override
    public boolean write(Object msg) {
        return channel.writeAndFlush(msg).isSuccess();
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public NetAddress address() {
        return remoteAddress;
    }

    @Override
    public void close() {
//        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(
//                ChannelFutureListener.CLOSE).isSuccess();
        channel.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T msg) throws Exception {
        messageReceived(msg);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        log.debug("channel unregistered,ctx={}", ctx);
        onClose();
    }

    /**
     * 当消息到达
     *
     * @param msg
     */
    public abstract void messageReceived(T msg);

    @Override
    public String toString() {
        return "NettyConnect{" +
                "channel=" + channel +
                ", remoteAddress=" + remoteAddress +
                '}';
    }
}
