package com.tsixi.mars.rpc.server;

import com.tsixi.mars.net.NetAddress;
import com.tsixi.mars.config.NodeConfig;
import com.tsixi.mars.netty.NettyServer;
import com.tsixi.mars.netty.RpcChannelInitializer;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * RPC 服务器（用于发布 RPC 服务）
 *
 * @author alan
 * @since 1.0.0
 */
@Component
public class RpcServer implements ApplicationListener<ContextRefreshedEvent>,
        CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RpcServer.class);

    NodeConfig nodeConfig;

    /**
     * RPC 服务映射
     */
    private Map<String, Object> handlerMap = new HashMap<>();

    @Autowired
    public RpcServer(NodeConfig nodeConfig) {
        this.nodeConfig = nodeConfig;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (nodeConfig.getRpcAddress() != null) {
            log.info("init rpc service...");
            ApplicationContext ctx = event.getApplicationContext();
            Map<String, Object> serviceBeanMap = ctx
                    .getBeansWithAnnotation(RpcService.class);
            if (MapUtils.isNotEmpty(serviceBeanMap)) {
                serviceBeanMap.values().forEach(e -> {
                    String interfaceName = e.getClass()
                            .getAnnotation(RpcService.class).value().getName();
                    handlerMap.put(interfaceName, e);
                    log.info("add rpc service {} - {}.", interfaceName,
                            e.getClass());
                });
            } else {
                log.info("RpcService bean map is null!");
            }
        } else {
            log.info("not has rpc config...");
        }
    }

    @Override
    public void run(String... args) throws Exception {
        if (nodeConfig.getRpcAddress() != null) {
            log.info("start rpc server.");
            NetAddress netAddress = nodeConfig.getRpcAddress();
            // 配置启动远程调用服务器
            RpcChannelInitializer initializer = new RpcChannelInitializer(new RpcHandler(handlerMap));
            int port = netAddress.getPort();
            NettyServer nettyServer = new NettyServer(netAddress.getHost(),
                    port, initializer);
            nettyServer.setName("rpc-nio-" + port);
            nettyServer.start();
        }
    }
}
