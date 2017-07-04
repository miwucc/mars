package com.tsixi.mars.rpc.client;

import com.tsixi.mars.net.NetAddress;
import com.tsixi.mars.netty.ConnectPool;
import com.tsixi.mars.rpc.client.proxy.IAsyncObjectProxy;
import com.tsixi.mars.rpc.client.proxy.ObjectProxy;
import org.apache.log4j.Logger;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * RPC 客户端（用于创建 RPC 服务代理）
 *
 * @author alan
 * @since 1.0.0
 */
public class RpcClient {

    static Logger log = Logger.getLogger(RpcClient.class);

    private ConnectPool<RpcConnect> rpcConnectPool;

    private NetAddress netAddress;

    private Map<Class<?>, Object> services = new HashMap<>();

    public RpcClient(NetAddress netAddress) {
        this.netAddress = netAddress;
        rpcConnectPool = new ConnectPool<RpcConnect>(netAddress, new RpcClientInitializer()).init();
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> interfaceClass) {
        Object obj = services.get(interfaceClass);
        if (obj == null) {
            obj = Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                    new Class<?>[]{interfaceClass},
                    new ObjectProxy<>(interfaceClass, rpcConnectPool));
            services.put(interfaceClass, obj);
        }
        return (T) obj;
    }

    public <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
        return new ObjectProxy<>(interfaceClass, rpcConnectPool);
    }

}
