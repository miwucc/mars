/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.maker;

import com.tsixi.mars.protobuf.PbMessageHandler;

/**
 * Created on 2017/4/20.
 *
 * @author Chow
 * @since 1.0
 */
public interface PbMessageHandlerProxy extends PbMessageHandler {

    void setBean(Object bean);

}
