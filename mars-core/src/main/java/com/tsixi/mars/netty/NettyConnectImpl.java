/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 2017/4/5.
 *
 * @author Alan
 * @since 1.0
 */
public class NettyConnectImpl extends NettyConnect {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onClose() {
        log.debug("connect on closed.");
    }

    @Override
    public void onCreate() {
        log.debug("connect on create");
    }

    @Override
    public void messageReceived(Object msg) {
        log.debug("message received...");
    }
}
