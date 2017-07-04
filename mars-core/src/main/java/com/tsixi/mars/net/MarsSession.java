/**
 * Copyright Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 * <p>
 * 2017年2月8日
 */
package com.tsixi.mars.net;

import com.google.protobuf.GeneratedMessage;
import com.tsixi.mars.protobuf.PbMessage.TXMessage;
import com.tsixi.mars.protobuf.PbMessageHandler;
import com.tsixi.mars.protobuf.PbMessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户session
 *
 * @author Alan
 * @scene 1.0
 */
public class MarsSession implements Session<TXMessage>, Connect, ConnectListener {

    protected Logger log = LoggerFactory.getLogger(getClass());

    protected String sessionId;

    protected Connect connect;

    protected NetAddress address;

    protected Object reference;

    protected PbMessageHandler handler;

    protected SessionListener sessionListener;

    public MarsSession(String id, NetAddress netAddress, Connect connect) {
        this.sessionId = id;
        this.address = netAddress;
        this.connect = connect;
    }

    public <T> T getReference(Class<T> clazz) {
        if (reference != null && reference.getClass().isAssignableFrom(clazz)) {
            return clazz.cast(reference);
        }
        return null;
    }

    public void setReference(Object reference) {
        this.reference = reference;
    }

    public void setHandler(PbMessageHandler handler) {
        this.handler = handler;
    }

    public void setSessionListener(SessionListener sessionListener) {
        this.sessionListener = sessionListener;
    }

    @Override
    public boolean write(Object msg) {
        if (msg instanceof GeneratedMessage) {
            send((GeneratedMessage) msg);
        }
        return true;
    }

    public void close() {
        if (connect != null && connect.isActive()) {
            connect.close();
        }
    }

    @Override
    public boolean isActive() {
        return connect.isActive();
    }

    @Override
    public NetAddress address() {
        return address;
    }

    @Override
    public void onClose() {
        if (sessionListener != null) {
            sessionListener.onSessionClose(this);
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public String sessionId() {
        return sessionId;
    }

    public void send(GeneratedMessage message) {
        TXMessage msg = null;
        try {
            msg = PbMessageHelper.genTXMessage(message);
        } catch (Exception e) {
            log.warn("", e);
        }
        boolean result = connect.write(msg);
        if (log.isDebugEnabled()) {
            log.debug("消息发送结果 cmd={},result={},address={},size={}", msg.getCmd(), result,
                    connect.address(), msg.getSerializedSize());
        }
    }

    @Override
    public void onReceive(Connect connect, TXMessage message) {
        handler.handle(this, message);
    }

    @Override
    public String toString() {
        return "MarsSession{sessionId=" + sessionId + ", connect=" + connect + ", address=" + address + '}';
    }

    @Override
    public void onConnectClose(Connect connect) {

    }
}
