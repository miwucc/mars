/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.cluster;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

/**
 * Created on 2017/3/31.
 *
 * @author Alan
 * @since 1.0
 */
public class ClusterConnectInitializer extends ChannelInitializer<SocketChannel> {
    private static final int MSG_MAX_SIZE = 10 * 1024 * 1024;

    private static final int HANDER_SIZE = 4;
    private ClusterClientInbox clusterClientInbox;

    private MessageLite messageLite;

    public ClusterConnectInitializer(ClusterClientInbox clusterClientInbox, MessageLite messageLite) {
        this.clusterClientInbox = clusterClientInbox;
        this.messageLite = messageLite;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(
                MSG_MAX_SIZE, 0, HANDER_SIZE, 0, 4))
                .addLast(new ProtobufDecoder(messageLite))
                .addLast(new LengthFieldPrepender(HANDER_SIZE))
                .addLast(new ProtobufEncoder())
                .addLast(new ClusterConnect(clusterClientInbox));
    }
}
