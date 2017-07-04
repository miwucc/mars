/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.cluster;

import com.tsixi.mars.curator.*;
import com.tsixi.mars.net.NetAddress;
import com.tsixi.mars.net.Session;
import com.tsixi.mars.netty.ConnectPool;
import com.tsixi.mars.netty.NettyServer;
import com.tsixi.mars.protobuf.PbMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 集群系统总线
 * <p>
 * Created on 2017/3/28.
 *
 * @author Alan
 * @since 1.0
 */
@Component
public class ClusterSystem implements MarsNodeListener {

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 集群各个节点连接池
     */
    private Map<NetAddress, ConnectPool> connectPoolMap = new HashMap<>();

    /**
     * 集群各个节点客户端对象
     */
    private Map<MarsNode, ClusterClient> clusterClientMap = new HashMap<>();

    /**
     * 玩家session集合
     */
    private Map<String, Session> sessionMap = new HashMap<>();

    /**
     * 消息收件箱
     */
    private ClusterClientInbox inbox;

    /**
     * 连接通道初始化器
     */
    private ClusterConnectInitializer initializer;

    @Autowired
    private NodeManager nodeManager;
    @Autowired
    private MarsCurator marsCurator;

    @Bean
    private ClusterClientInbox createClusterClientInbox() {
        inbox = new ClusterClientInbox(this);
        return inbox;
    }

    @Bean
    private ClusterConnectInitializer createClusterConnectInitializer() {
        initializer = new ClusterConnectInitializer(inbox, PbMessage.ClusterMessage.getDefaultInstance());
        return initializer;
    }

    public Map<String, Session> sessionMap() {
        return sessionMap;
    }

    public ClusterClient getByNodeType(NodeType nodeType) {
        MarsNode marsNode = nodeManager.getMarNode(nodeType);
        if (marsNode == null || !marsNode.hasChildren()) {
            log.warn("node not found or not has children,tpye is {}", nodeType);
            return null;
        }
        MarsNode randomOneMarsNode = marsNode.randomOneMarsNode();
        return get(randomOneMarsNode);
    }

    public ClusterClient get(MarsNode marsNode) {
        return clusterClientMap.computeIfAbsent(marsNode, e -> {
            ClusterClient clusterClient = new ClusterClient(e, this);
            marsCurator.addMarsNodeListener(e.getNodePath(), this);
            return clusterClient;
        });
    }

    public ConnectPool getMarsConnectPool(NetAddress netAddress) {
        return connectPoolMap.computeIfAbsent(netAddress, e -> new ConnectPool(e, initializer).init());
    }

    public void startClusterServer(NetAddress netAddress) {
        NettyServer nettyServer = new NettyServer(netAddress.getPort(), initializer);
        nettyServer.setName("tcp-nio-" + netAddress.getPort());
        nettyServer.start();
    }

    @Override
    public void nodeChange(NodeChangeType nodeChangeType, MarsNode marsNode) {
        ClusterClient clusterClient = clusterClientMap.get(marsNode);
        if (clusterClient == null) {
            return;
        }
        switch (nodeChangeType) {
            case NODE_ADD:
                clusterClient.init(marsNode);
                break;
            case NODE_REMOVE:
                clusterClientMap.remove(marsNode);
                marsCurator.removeMarsNodeListener(marsNode.getNodePath(), this);
                break;
        }
    }
}
