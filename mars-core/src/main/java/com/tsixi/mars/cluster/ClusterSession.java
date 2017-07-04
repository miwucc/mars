/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.cluster;

import com.google.protobuf.GeneratedMessage;
import com.tsixi.mars.cluster.pb.SessionMessage.SessionQuit;
import com.tsixi.mars.net.Connect;
import com.tsixi.mars.net.MarsSession;
import com.tsixi.mars.net.NetAddress;
import com.tsixi.mars.protobuf.PbMessage.ClusterMessage;
import com.tsixi.mars.protobuf.PbMessageHelper;

/**
 * Created on 2017/4/11.
 *
 * @author Alan
 * @since 1.0
 */
public class ClusterSession extends MarsSession {
    public ClusterSession(String id, NetAddress netAddress, Connect connect) {
        super(id, netAddress, connect);
    }

    @Override
    public void send(GeneratedMessage message) {
        ClusterMessage msg = null;
        try {
            msg = PbMessageHelper.genClusterMessage(sessionId, message);
        } catch (Exception e) {
            log.warn("message send error,sessionId is {}", sessionId, e);
        }
        boolean result = connect.write(msg);
        if (log.isDebugEnabled()) {
            log.debug("消息发送结果 cmd={},result={},address={},size={}", msg.getMsg().getCmd(), result,
                    address,msg.getSerializedSize());
        }
    }

    @Override
    public void close() {
        SessionQuit sessionQuit = SessionQuit.newBuilder().setSessionId(sessionId()).build();
        try {
            ClusterMessage clusterMessage = PbMessageHelper.genClusterMessage(null, sessionQuit);
            connect.write(clusterMessage);
        } catch (Exception e) {
            log.warn("session close error,sessionId is {}", sessionId, e);
        }
    }
}
