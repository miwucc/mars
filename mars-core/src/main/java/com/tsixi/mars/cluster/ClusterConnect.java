/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.cluster;

import com.tsixi.mars.net.Connect;
import com.tsixi.mars.netty.NettyConnect;
import com.tsixi.mars.protobuf.PbMessage;

/**
 * Created on 2017/3/27.
 *
 * @author Alan
 * @since 1.0
 */
public class ClusterConnect extends NettyConnect<PbMessage.ClusterMessage> implements Connect {

    private ClusterClientInbox clusterClientInbox;

    public ClusterConnect(ClusterClientInbox clusterClientInbox) {
        this.clusterClientInbox = clusterClientInbox;
    }

    @Override
    public void messageReceived(PbMessage.ClusterMessage msg) {
        clusterClientInbox.onReceive(this, msg);
    }

    @Override
    public void onClose() {

    }

    @Override
    public void onCreate() {
    }
}
