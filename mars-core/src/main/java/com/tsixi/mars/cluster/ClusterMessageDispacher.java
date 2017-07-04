/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.cluster;

import com.tsixi.mars.net.Connect;
import com.tsixi.mars.net.Inbox;
import com.tsixi.mars.protobuf.PbMessage;
import com.tsixi.mars.protobuf.PbMessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2017/4/6.
 *
 * @author Alan
 * @since 1.0
 */
@Component
public class ClusterMessageDispacher implements Inbox<PbMessage.TXMessage>, ApplicationListener<ContextRefreshedEvent> {

    private Logger log = LoggerFactory.getLogger(getClass());

    private Map<Integer, ClusterMessageHandler> messageHandlers = new HashMap<>();

    private Class<ClusterHandler> aclazz = ClusterHandler.class;

    @Override
    public void onReceive(Connect connect, PbMessage.TXMessage message) {
        int messageType = message.getMessageType();
        ClusterMessageHandler handler = messageHandlers.get(messageType);
        if (handler != null) {
            try {
                handler.onReceive(connect, message);
            } catch (Exception e) {
                log.warn("消息处理失败，messageType={}", messageType, e);
            }
        } else {
            log.warn("找不到处理器，messageType={}", messageType);
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.debug("开始初始化 {} 集群消息分发器", aclazz);
        ApplicationContext context = event.getApplicationContext();
        Map<String, Object> beans = context.getBeansWithAnnotation(aclazz);
        List<ClusterMessageHandler> handlers = PbMessageHelper.findHandler(beans.values(), ClusterMessageHandler.class);
        handlers.forEach(handler -> {
            int messageType = handler.getMessageType();
            messageHandlers.put(messageType, handler);
            log.info("注册 {} 消息处理 {} -> {} ",
                    aclazz.getSimpleName(), messageType,
                    handler);
        });
    }
}
