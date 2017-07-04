/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.net;

/**
 * Created on 2017/3/28.
 *
 * @author Alan
 * @since 1.0
 */
public interface Inbox<T> {

    void onReceive(Connect connect, T message);


}
