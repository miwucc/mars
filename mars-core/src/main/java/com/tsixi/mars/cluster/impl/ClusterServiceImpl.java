/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.cluster.impl;

import com.tsixi.mars.cluster.ClusterService;
import com.tsixi.mars.cluster.ClusterSystem;
import com.tsixi.mars.net.MarsSession;
import com.tsixi.mars.net.NetAddress;
import com.tsixi.mars.rpc.server.RpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created on 2017/3/31.
 *
 * @author Alan
 * @since 1.0
 */
@Component
@RpcService(ClusterService.class)
public class ClusterServiceImpl implements ClusterService {

    private ClusterSystem clusterSystem;

    @Autowired
    public ClusterServiceImpl(ClusterSystem clusterSystem) {
        this.clusterSystem = clusterSystem;
    }

    @Override
    public boolean sessionEnter(String sessionId, NetAddress netAddress) {
//        MarsSession marsSession = new MarsSession(sessionId, netAddress);
//        clusterSystem.sessionMap().put(sessionId, marsSession);
//        marsSession.onCreate();MarsSession marsSession = new MarsSession(sessionId, netAddress);
//        clusterSystem.sessionMap().put(sessionId, marsSession);
//        marsSession.onCreate();
        return true;
    }

    @Override
    public boolean sessionQuit(String sessionId) {
        MarsSession marsSession = (MarsSession) clusterSystem.sessionMap().remove(sessionId);
        if (marsSession != null) {
            marsSession.onClose();
        }
        return true;
    }

    @Override
    public void messageReceive(String sessionId, Object msg) {
        MarsSession marsSession = (MarsSession) clusterSystem.sessionMap().get(sessionId);
        if (marsSession != null) {
        }
    }
}
