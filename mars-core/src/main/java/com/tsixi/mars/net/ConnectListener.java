/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.net;

/**
 * 连接监听器
 * <p>
 * Created on 2017/3/29.
 *
 * @author Alan
 * @since 1.0
 */
public interface ConnectListener {

    /**
     * 当连接被关闭
     *
     * @param connect
     */
    void onConnectClose(Connect connect);
}
