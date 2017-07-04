package com.tsixi.mars.rpc.client;

import com.tsixi.mars.rpc.protocol.RpcDecoder;
import com.tsixi.mars.rpc.protocol.RpcEncoder;
import com.tsixi.mars.rpc.protocol.RpcRequest;
import com.tsixi.mars.rpc.protocol.RpcResponse;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * RPC连接初始器
 * <p>
 * Created on 2016-03-16.
 *
 * @author Alan
 * @since 1.0
 */
public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();
        cp.addLast(new RpcEncoder(RpcRequest.class));
        cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        cp.addLast(new RpcDecoder(RpcResponse.class));
        cp.addLast(new RpcConnect());
    }
}
