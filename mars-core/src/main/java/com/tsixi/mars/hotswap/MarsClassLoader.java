/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.hotswap;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created on 2017/3/10.
 *
 * @author Alan
 * @since 1.0
 */
public class MarsClassLoader extends URLClassLoader {
    public MarsClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
}
