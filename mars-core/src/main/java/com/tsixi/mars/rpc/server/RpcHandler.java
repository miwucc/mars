package com.tsixi.mars.rpc.server;

import com.tsixi.mars.rpc.protocol.RpcRequest;
import com.tsixi.mars.rpc.protocol.RpcResponse;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * RPC 处理器（用于处理 RPC 请求）
 *
 * @author alan
 * @since 1.0.0
 */
@Sharable
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger log = LoggerFactory.getLogger(RpcHandler.class);

    private final Map<String, Object> handlerMap;

    public RpcHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx,
                             final RpcRequest request) throws Exception {
        log.debug("Receive request " + request.getRequestId());
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            Object result = handle(request);
            response.setResult(result);
        } catch (Throwable t) {
            response.setError(t.toString());
            log.error("RPC Server handle request error", t);
        }
        ctx.writeAndFlush(response).addListener(e ->
                log.debug("Send response for request " + request.getRequestId())
        );
    }

    private Object handle(RpcRequest request) throws Throwable {
        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        log.debug(serviceClass.getName());
        log.debug(methodName);
        for (int i = 0; i < parameterTypes.length; ++i) {
            log.debug(parameterTypes[i].getName());
        }
        for (int i = 0; i < parameters.length; ++i) {
            log.debug(parameters[i].toString());
        }

        // JDK reflect
        // Method method = serviceClass.getMethod(methodName, parameterTypes);
        // method.setAccessible(true);
        // return method.invoke(serviceBean, parameters);

        // Cglib reflect
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName,
                parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server caught exception", cause);
        ctx.close();
    }
}
