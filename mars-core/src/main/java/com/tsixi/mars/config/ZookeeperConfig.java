/**
 * Copyright Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 *
 * 2017年3月2日 	
 */
package com.tsixi.mars.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * zk 配置信息
 * 
 * @scene 1.0
 * 
 * @author Alan
 *
 */
@Component
@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperConfig {
	/**
	 * 连接字符串
	 */
	String connects;
	/**
	 * 连接间隔时间
	 */
	int baseSleepTimeMs;
	/**
	 * 最大重试次数
	 */
	int maxRetries;

	/**
	 * 根目录
	 */
	String marsRoot;

	public String getConnects() {
		return connects;
	}

	public void setConnects(String connects) {
		this.connects = connects;
	}

	public String getMarsRoot() {
		return marsRoot;
	}

	public void setMarsRoot(String marsRoot) {
		this.marsRoot = marsRoot;
	}

	public int getBaseSleepTimeMs() {
		return baseSleepTimeMs;
	}

	public void setBaseSleepTimeMs(int baseSleepTimeMs) {
		this.baseSleepTimeMs = baseSleepTimeMs;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

}
