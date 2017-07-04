/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.cluster;

import com.tsixi.mars.net.NetAddress;

/**
 * <p>
 * 集群服务
 * <p>
 * Created on 2017/3/31.
 *
 * @author Alan
 * @since 1.0
 */
public interface ClusterService {

    /**
     * 用户进入
     *
     * @param sessionId
     * @param netAddress
     * @return
     */
    boolean sessionEnter(String sessionId, NetAddress netAddress);

    /**
     * 用户退出
     * 
     * @param sessionId
     * @return
     */
    boolean sessionQuit(String sessionId);

    /**
     * 用户消息到达
     * 
     * @param sessionId
     * @param msg
     */
    void messageReceive(String sessionId, Object msg);
}
