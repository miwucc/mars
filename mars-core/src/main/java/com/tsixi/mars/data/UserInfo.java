/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.data;

/**
 * Created on 2017/4/13.
 *
 * @author Alan
 * @since 1.0
 */
public class UserInfo {
    private String sessionId;
    private long accountId;
    private long roleUid;
    private String nodePath;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getRoleUid() {
        return roleUid;
    }

    public void setRoleUid(long roleUid) {
        this.roleUid = roleUid;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }
}
