/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.cluster;

import com.alibaba.fastjson.JSON;
import com.tsixi.mars.config.NodeConfig;
import com.tsixi.mars.curator.MarsNode;
import com.tsixi.mars.net.Connect;
import com.tsixi.mars.netty.ConnectPool;
import com.tsixi.mars.rpc.client.RpcClient;

/**
 * <p>
 * 集群客户端对象
 * </p>
 * <p>
 * Created on 2017/3/28.
 *
 * @author Alan
 * @since 1.0
 */
public class ClusterClient {
    /**
     * 节点的配置信息
     */
    private NodeConfig nodeConfig;
    /**
     * 集群节点信息
     */
    private MarsNode marsNode;
    /**
     * 连接池
     */
    private ConnectPool connectPool;

    private RpcClient rpcClient;
    private ClusterSystem clusterSystem;

    public ClusterClient(MarsNode marsNode, ClusterSystem clusterSystem) {
        this.clusterSystem = clusterSystem;
        init(marsNode);
    }

    public void init(MarsNode marsNode) {
        this.marsNode = marsNode;
        nodeConfig = JSON.parseObject(marsNode.getNodeData(), NodeConfig.class);
        this.connectPool = clusterSystem.getMarsConnectPool(nodeConfig.getTcpAddress());
        rpcClient = new RpcClient(nodeConfig.getRpcAddress());
    }

    public RpcClient getRpcClient() {
        return rpcClient;
    }

    public Connect getConnect() {
        return connectPool.getConnect();
    }

    public void write(Object msg) {
        connectPool.getConnect().write(msg);
    }

}
