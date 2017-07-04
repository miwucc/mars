/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.helper;

import com.tsixi.mars.sample.Sample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 样本文件处理帮助者
 *
 * Created on 2017/3/15.
 *
 * @author Alan
 * @since 1.0
 */
public class SampleHelper {
    public final Logger log = LoggerFactory.getLogger(getClass());

    public List<Sample> resolveSample(Class<Sample> clazz,
            String[] attributeNames, List<String[]> attributeValues)
            throws Exception {
        List<Sample> samples = new ArrayList<Sample>(attributeValues.size());
        Sample sample = null;
        int n = 2;
        for (String[] values : attributeValues) {
            n++;
            try {
                sample = clazz.newInstance();
            } catch (Exception e) {
                log.warn("", e);
            }
            if (values.length != attributeNames.length) {
                log.warn("数据异常，值列表长度与标题列表长度不一致" + ",class=" + clazz.getName()
                        + ",第 " + n + " 行数据异常[" + values + "]");
                throw new RuntimeException();
            }
            for (int i = 0; i < values.length; i++) {
                if (attributeNames[i] == null || attributeNames[i].isEmpty()
                        || values[i] == null || values[i].isEmpty()) {
                    continue;
                }
                String attributeName = attributeNames[i].trim();
                String attributeValue = values[i].trim();
                if (!(sample.setAttribute(attributeName, attributeValue)
                        || reflexCallMethod(sample, "set" + attributeName,
                                attributeValue)
                        || reflexCallMethod(sample, "is" + attributeName,
                                attributeValue))) {
                    log.debug("字段未被成功读取，class=" + clazz + ",attributeName="
                            + attributeName + ",attributeValue="
                            + attributeValue);
                }
            }
            if (sample.getSid() > 0) {
                samples.add(sample);
            }
        }
        return samples;
    }

    private boolean reflexCallMethod(Object obj, String methodName,
            String value) {
        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase(methodName)) {
                @SuppressWarnings("rawtypes")
                Class[] pms = method.getParameterTypes();
                if (pms == null || pms.length == 0) {
                    continue;
                }
                Object pObject = null;
                if (pms[0] == String.class) {
                    pObject = value;
                } else if (pms[0] == int.class) {
                    pObject = Integer.valueOf(value);
                } else if (pms[0] == byte.class) {
                    pObject = Byte.valueOf(value);
                } else if (pms[0] == boolean.class) {
                    pObject = Boolean.valueOf(value);
                } else if (pms[0] == short.class) {
                    pObject = Short.valueOf(value);
                } else if (pms[0] == long.class) {
                    pObject = Long.valueOf(value);
                } else if (pms[0] == float.class) {
                    pObject = Float.valueOf(value);
                } else if (pms[0] == double.class) {
                    pObject = Double.valueOf(value);
                }
                try {
                    if (pObject != null) {
                        method.invoke(obj, pObject);
                        return true;
                    }
                } catch (Exception e) {
                    log.warn("method invoke error.", e);
                }
                return false;
            }
        }
        return false;
    }
}
