/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import com.tsixi.mars.protobuf.PbMessage.ClusterMessage;
import com.tsixi.mars.protobuf.PbMessage.TXMessage;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * pb 消息帮助器
 * <p>
 * Created on 2017/4/1.
 *
 * @author Alan
 * @since 1.0
 */
public final class PbMessageHelper {

    public static <T> List<T> findHandler(Collection<Object> beans, Class<T> clazz) {
        if (beans == null || beans.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<T> tmps = new ArrayList<>();
        for (Object obj : beans) {
            if (clazz.isInstance(obj)) {
                T handler = clazz.cast(obj);
                tmps.add(handler);
            }
        }
        return tmps;
    }

    public static TXMessage genTXMessage(GeneratedMessage message) throws Exception {
        TXMessage.Builder txBuilder = TXMessage.newBuilder()
                .setDataMessage(message.toByteString());
        Descriptors.Descriptor descriptor = message.getDescriptorForType();
        Descriptors.FieldDescriptor fieldDescriptor = descriptor
                .findFieldByName("message_type");
        if (fieldDescriptor != null && fieldDescriptor.hasDefaultValue()) {
            Object value = fieldDescriptor.getDefaultValue();
            txBuilder.setMessageType(Integer.parseInt(value.toString()));
        } else {
            throw new IllegalArgumentException("找不到默认的消息类型设置，请检查 " + descriptor.getFullName() + "消息协议的定义");
        }
        fieldDescriptor = descriptor.findFieldByName("cmd");
        if (fieldDescriptor != null && fieldDescriptor.hasDefaultValue()) {
            Object value = fieldDescriptor.getDefaultValue();
            txBuilder.setCmd(Integer.parseInt(value.toString()));
        }

        return txBuilder.build();
    }

    public static ClusterMessage genClusterMessage(String sessionId, GeneratedMessage message) throws Exception {
        TXMessage msg = genTXMessage(message);
        ClusterMessage.Builder builder = ClusterMessage.newBuilder().setMsg(msg);
        if (!StringUtils.isEmpty(sessionId)) {
            builder.setTargetId(sessionId);
        }
        return builder.build();
    }

}
