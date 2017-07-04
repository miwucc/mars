/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.rpc.client;

/**
 * Created on 2017/3/8.
 *
 * @author Alan
 * @since 1.0
 */
public interface RpcServiceFactory {

    <T> T create(Class<T> interfaceClass);
}
