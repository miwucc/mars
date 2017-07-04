/**
 * Copyright Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 *
 * 2017年2月27日 	
 */
package com.tsixi.mars.data;

/**
 *
 * 
 * @scene 1.0
 * 
 * @author Alan
 *
 */
public class ServerInfo {

    protected String host;

    protected int port;

    public ServerInfo() {
    }

    public ServerInfo(String host, int port) {
        super();
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
