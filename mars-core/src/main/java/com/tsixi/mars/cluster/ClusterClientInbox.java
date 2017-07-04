/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.cluster;

import com.tsixi.mars.net.Connect;
import com.tsixi.mars.net.Inbox;
import com.tsixi.mars.protobuf.PbMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * <p>集群消息收件箱
 * <p>1、将玩家消息交由玩家session
 * <p>2、将集群消息进行分发处理
 * Created on 2017/3/27.
 *
 * @author Alan
 * @since 1.0
 */
public class ClusterClientInbox
        implements Inbox<PbMessage.ClusterMessage> {
    private Logger log = LoggerFactory.getLogger(getClass());

    private ClusterSystem clusterSystem;

    /**
     * 集群内部消息处理器
     */
    @Autowired
    private ClusterMessageDispacher dispacher;

    public ClusterClientInbox(ClusterSystem clusterSystem) {
        this.clusterSystem = clusterSystem;
    }

    @Override
    public void onReceive(Connect connect,
                          PbMessage.ClusterMessage msg) {
        String id = msg.getTargetId();
        PbMessage.TXMessage message = msg.getMsg();
        if (!StringUtils.isEmpty(id)) {
            Inbox<PbMessage.TXMessage> sessionInbox = clusterSystem.sessionMap().get(id);
            if (sessionInbox != null) {
                sessionInbox.onReceive(connect, message);
            } else {
                log.warn("message has not handle,channel id is {}", id);
            }
        } else {
            dispacher.onReceive(connect, message);
        }
    }
}
