/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.hotswap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created on 2017/3/10.
 *
 * @author Alan
 * @since 1.0
 */
@Component
@ConfigurationProperties(prefix = "plugin")
public class PluginConfig {

    String dir;

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}
