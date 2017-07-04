/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

/**
 * Created on 2017/3/10.
 *
 * @author Alan
 * @since 1.0
 *
 *        <p>
 *        该包作为mars框架热更接口定义包，需要被热更新的类不能包含任何额外的数据类，本框架的热更方案为：
 *        <p>
 *        1、接口提前定义，实现类由单独的classloader进行加载。
 *        <p>
 *        2、需要被热更新的包单独放置到根目录下hotswap的目录中，由程序启动后扫描加载，并且在有更新后重新加载.
 *        <p>
 *        3、每个包中需要提供一个启动类，以用于在包被加载后初始化行为
 */
package com.tsixi.mars.hotswap;