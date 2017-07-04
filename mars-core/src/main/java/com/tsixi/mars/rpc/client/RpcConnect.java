package com.tsixi.mars.rpc.client;

import com.tsixi.mars.netty.NettyConnect;
import com.tsixi.mars.rpc.protocol.RpcRequest;
import com.tsixi.mars.rpc.protocol.RpcResponse;
import io.netty.channel.ChannelHandler.Sharable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2016-03-14.
 *
 * @author Alan
 * @since 1.0
 */
@Sharable
public class RpcConnect extends NettyConnect<RpcResponse> {
    private static final Logger log = LoggerFactory.getLogger(RpcConnect.class);

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,
            2, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));

    private ConcurrentHashMap<String, RPCFuture> pendingRPC = new ConcurrentHashMap<>();

    @Override
    public void messageReceived(RpcResponse response) {
        String requestId = response.getRequestId();
        RPCFuture rpcFuture = pendingRPC.get(requestId);
        if (rpcFuture != null) {
            pendingRPC.remove(requestId);
            rpcFuture.done(response);
        }
    }

    @Override
    public void onClose() {
        threadPoolExecutor.shutdown();
    }

    @Override
    public void onCreate() {

    }

    /**
     * 发送消息，并将消息保存等待返回列表
     *
     * @param request
     * @return
     */
    public RPCFuture sendRequest(RpcRequest request) {
        RPCFuture rpcFuture = new RPCFuture(request, this);
        pendingRPC.put(request.getRequestId(), rpcFuture);
        channel.writeAndFlush(request);
        return rpcFuture;
    }

    public void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }

}
