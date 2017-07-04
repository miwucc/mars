/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.gate;

import com.google.protobuf.ByteString;
import com.tsixi.mars.cluster.ClusterHandler;
import com.tsixi.mars.cluster.ClusterMessageHandler;
import com.tsixi.mars.cluster.ClusterSystem;
import com.tsixi.mars.cluster.pb.SessionMessage.SessionQuit;
import com.tsixi.mars.net.Connect;
import com.tsixi.mars.protobuf.PbMessage.TXMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created on 2017/4/11.
 *
 * @author Alan
 * @since 1.0
 */
@ClusterHandler
public class GateSessionMessageHandler implements ClusterMessageHandler<TXMessage> {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ClusterSystem clusterSystem;

    @Override
    public void onReceive(Connect connect, TXMessage message) {
        if (message.getCmd() == SessionQuit.getDefaultInstance().getCmd()) {
            sessionQuit(connect, message.getDataMessage());
        }
    }

    public void sessionQuit(Connect connect, ByteString bs) {
        try {
            SessionQuit sessionQuit = SessionQuit.parseFrom(bs);
            String sessionId = sessionQuit.getSessionId();
            log.debug("session quit,sessionId={},address={}", sessionId);
            GateSession gateSession = (GateSession) clusterSystem.sessionMap().remove(sessionId);
            if (gateSession != null) {
                gateSession.close();
            }
        } catch (Exception e) {
            log.warn("session quit...", e);
        }
    }

    @Override
    public int getMessageType() {
        return SessionQuit.getDefaultInstance().getMessageType();
    }
}
