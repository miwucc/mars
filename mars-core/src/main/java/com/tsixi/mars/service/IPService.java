/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.service;

/**
 * Created on 2017/3/17.
 * ip有关的服务
 * @author Chow
 * @since 1.0
 */
public interface IPService {
    /**
     * 通过IP地址获取国家代码
     * @param IP IP地址 例如: 192.168.2.23
     * @return 国家代码 例如:HK CH USA
     */
    String getCountryIdByIP(String IP);

}
