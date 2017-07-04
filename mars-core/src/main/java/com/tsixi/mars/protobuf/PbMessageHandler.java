/**
 * Copyright Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 * <p>
 * 2017年2月8日
 */
package com.tsixi.mars.protobuf;

import com.tsixi.mars.net.MarsSession;
import com.tsixi.mars.protobuf.PbMessage.TXMessage;

/**
 *
 * pb类型消息处理器接口
 *
 * @scene 1.0
 *
 * @author Alan
 *
 */
public interface PbMessageHandler {

    /**
     * 处理器可处理的消息类型值
     *
     * @return
     */
    int getMessageType();

    /**
     * 消息处理方法
     *
     * @param session
     * @param msg
     */
    void handle(MarsSession session, TXMessage msg);
}
