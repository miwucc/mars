/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.uid;

/**
 * Created on 2017/3/8.
 *
 * @author Alan
 * @since 1.0
 */
public class UidCache {
    public long currentUid;

    public long maxUid;

    public UidCache(long currentUid, long maxUid) {
        this.currentUid = currentUid;
        this.maxUid = maxUid;
    }
}
