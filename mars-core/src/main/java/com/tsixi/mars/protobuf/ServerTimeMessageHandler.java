/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.protobuf;

import com.tsixi.mars.net.MarsSession;
import com.tsixi.mars.protobuf.PbMessage.ReqServerTime;
import com.tsixi.mars.protobuf.PbMessage.ResServerTime;
import org.springframework.stereotype.Component;

/**
 * Created on 2017/4/21.
 *
 * @author Alan
 * @since 1.0
 */
@Component
@ServerHandler
public class ServerTimeMessageHandler implements PbMessageHandler {
    @Override
    public int getMessageType() {
        return ReqServerTime.getDefaultInstance().getMessageType();
    }

    @Override
    public void handle(MarsSession session, PbMessage.TXMessage msg) {
        ResServerTime rpm = ResServerTime.newBuilder().setTime(System.currentTimeMillis())
                .build();
        session.send(rpm);
    }
}
