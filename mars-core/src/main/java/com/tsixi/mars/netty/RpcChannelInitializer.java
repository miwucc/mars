/**
 * Copyright Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 * <p>
 * 2016年5月31日
 */
package com.tsixi.mars.netty;

import com.tsixi.mars.rpc.protocol.RpcDecoder;
import com.tsixi.mars.rpc.protocol.RpcEncoder;
import com.tsixi.mars.rpc.protocol.RpcRequest;
import com.tsixi.mars.rpc.protocol.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author Alan
 * @version 1.0
 */
public class RpcChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 业务处理器
     */
    private ChannelHandler channelHandler;

    public RpcChannelInitializer(
            ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                .addLast(new RpcDecoder(RpcRequest.class))
                .addLast(new RpcEncoder(RpcResponse.class))
                .addLast(channelHandler);

    }

}
