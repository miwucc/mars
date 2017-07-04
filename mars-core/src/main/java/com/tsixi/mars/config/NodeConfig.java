/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.config;

import com.tsixi.mars.net.NetAddress;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 集群节点配置信息
 * <p>
 * Created on 2017/3/6.
 *
 * @author Alan
 * @since 1.0
 */
@Component
@ConfigurationProperties(prefix = "cluster")
public class NodeConfig {
    /**
     * 节点默认父目录
     */
    protected String parentPath = "cluster";
    /**
     * 节点类型
     */
    protected String type;
    /**
     * 节点名称
     */
    protected String name;
    /**
     * 节点编号
     */
    protected int number;
    /**
     * 是否使用网关
     */
    protected boolean useGate;
    /**
     * 节点tcp服务地址
     */
    protected NetAddress tcpAddress;
    /**
     * 节点web服务地址
     */
    protected NetAddress httpAddress;
    /**
     * 节点rpc服务地址
     */
    protected NetAddress rpcAddress;

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public NetAddress getTcpAddress() {
        return tcpAddress;
    }

    public void setTcpAddress(NetAddress tcpAddress) {
        this.tcpAddress = tcpAddress;
    }

    public NetAddress getHttpAddress() {
        return httpAddress;
    }

    public void setHttpAddress(NetAddress httpAddress) {
        this.httpAddress = httpAddress;
    }

    public NetAddress getRpcAddress() {
        return rpcAddress;
    }

    public void setRpcAddress(NetAddress rpcAddress) {
        this.rpcAddress = rpcAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isUseGate() {
        return useGate;
    }

    public void setUseGate(boolean useGate) {
        this.useGate = useGate;
    }
}
