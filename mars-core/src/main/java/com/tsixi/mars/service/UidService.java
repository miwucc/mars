/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.service;

import com.tsixi.mars.uid.UidTypeEnum;

/**
 * Created on 2017/3/8.
 *
 * @author Alan
 * @since 1.0
 */
public interface UidService {
    /**
     * 获得一个long值类型的ID,并自增给定的数量
     *
     * @param key
     *            id键值
     * @param num
     *            获取数量
     * @return
     *
     * @exception RuntimeException
     *                当传入的数量不是自然数并且超过Int最大值
     */
    long getAndUpdateUid(UidTypeEnum key, int num);
}
