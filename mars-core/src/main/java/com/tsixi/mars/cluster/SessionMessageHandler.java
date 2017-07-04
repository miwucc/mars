/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.cluster;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tsixi.mars.cluster.pb.SessionMessage.Address;
import com.tsixi.mars.cluster.pb.SessionMessage.SessionQuit;
import com.tsixi.mars.net.Connect;
import com.tsixi.mars.net.MarsSession;
import com.tsixi.mars.net.NetAddress;
import com.tsixi.mars.net.SessionListener;
import com.tsixi.mars.protobuf.PbMessage.TXMessage;
import com.tsixi.mars.protobuf.PbMessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.tsixi.mars.cluster.pb.SessionMessage.SessionEnter;

/**
 * Created on 2017/4/6.
 *
 * @author Alan
 * @since 1.0
 */
@ClusterHandler
public class SessionMessageHandler implements ClusterMessageHandler<TXMessage> {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ClusterSystem clusterSystem;
    @Autowired
    private PbMessageDispatcher messageDispatcher;
    @Autowired
    private SessionListener sessionListener;

    @Override
    public void onReceive(Connect connect, TXMessage message) {
        if (message.getCmd() == SessionEnter.getDefaultInstance().getCmd()) {
            sessionEnter(connect, message.getDataMessage());
        } else if (message.getCmd() == SessionQuit.getDefaultInstance().getCmd()) {
            sessionQuit(connect, message.getDataMessage());
        }
    }

    public void sessionEnter(Connect connect, ByteString bs) {
        try {
            SessionEnter sessionEnter = SessionEnter.parseFrom(bs);
            String sessionId = sessionEnter.getSessionId();
            Address address = sessionEnter.getAddress();
            NetAddress netAddress = new NetAddress(address.getHost(), address.getPort());
            log.debug("session enter,sessionId={},address={}", sessionId, netAddress);
            MarsSession marsSession = new ClusterSession(sessionId, netAddress, connect);
            marsSession.setHandler(messageDispatcher);
            marsSession.setSessionListener(sessionListener);
            clusterSystem.sessionMap().put(marsSession.sessionId(), marsSession);
            marsSession.onCreate();
        } catch (Exception e) {
            log.warn("session enter error...", e);
        }
    }

    public void sessionQuit(Connect connect, ByteString bs) {
        try {
            SessionQuit sessionQuit = SessionQuit.parseFrom(bs);
            String sessionId = sessionQuit.getSessionId();
            log.debug("session quit,sessionId={},address={}", sessionId);
            MarsSession marsSession = (MarsSession) clusterSystem.sessionMap().remove(sessionId);
            if (marsSession != null) {
                marsSession.onClose();
                marsSession.setSessionListener(null);
                marsSession.setHandler(null);
                marsSession.setReference(null);
            }
        } catch (InvalidProtocolBufferException e) {
            log.warn("session quit...", e);
        }
    }

    @Override
    public int getMessageType() {
        return SessionQuit.getDefaultInstance().getMessageType();
    }
}
