/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.net;

/**
 * <p>连接接口</p>
 * <p>
 * Created on 2017/3/27.
 *
 * @author Alan
 * @since 1.0
 */
public interface Connect<T> {

    /**
     * 写消息方法
     *
     * @param msg
     * @return
     */
    boolean write(T msg);

    /**
     * 关闭连接
     *
     * @return
     */
    void close();

    /**
     * 连接是否有效
     *
     * @return
     */
    boolean isActive();

    /**
     * 连接的地址
     *
     * @return
     */
    NetAddress address();

    /**
     * 当连接关闭
     */
    void onClose();

    /**
     * 当连接被创建
     */
    void onCreate();
}
