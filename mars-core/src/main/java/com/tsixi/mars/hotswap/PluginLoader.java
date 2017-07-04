/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.hotswap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created on 2017/3/10.
 *
 * @author Alan
 * @since 1.0
 * 
 * 
 *        <p>
 *        插件加载器，框架初始化完成后运行并加载
 *        </p>
 */
@Component
public class PluginLoader implements CommandLineRunner {

    Logger log = LoggerFactory.getLogger(getClass());

    PluginConfig pluginConfig;

    @Autowired
    public PluginLoader(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    @Override
    public void run(String... args) throws Exception {
        if (pluginConfig == null) {
            log.debug("plugin has not config...");
            return;
        }
        String dir = pluginConfig.getDir();
    }
}
