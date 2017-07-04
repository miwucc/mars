/**
 * Copyright Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 * <p>
 * 2017年3月1日
 */
package com.tsixi.mars.protobuf;

import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

/**
 * <p>
 * 消息头固定字节数消息处理器，本类采用固定4字节头(int),头信息不包含头本身字节数的方式定义
 * </p>
 * <p>
 * 消息包大小限定为2M类，超出后会抛出异常
 * </p>
 *
 * @author Alan
 * @scene 1.0
 */
public class LengthFieldChannelInitializer
        extends ChannelInitializer<SocketChannel> {

    private static final int MSG_MAX_SIZE = 2 * 1024 * 1024;

    private static final int HANDER_SIZE = 4;

    private ChannelHandler messageDispatcher;

    private MessageLite messageLite;

    public LengthFieldChannelInitializer(ChannelHandler messageDispatcher) {
        this(messageDispatcher, PbMessage.TXMessage.getDefaultInstance());
    }

    public LengthFieldChannelInitializer(ChannelHandler messageDispatcher,
                                         MessageLite messageLite) {
        super();
        this.messageLite = messageLite;
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        // 解码用
        p.addLast(new LengthFieldBasedFrameDecoder(
                MSG_MAX_SIZE, 0, HANDER_SIZE, 0, 4));
        p.addLast(new ProtobufDecoder(messageLite));
        // 编码用
        p.addLast(new LengthFieldPrepender(HANDER_SIZE));
        p.addLast(new ProtobufEncoder());
        p.addLast(messageDispatcher);
    }

}
