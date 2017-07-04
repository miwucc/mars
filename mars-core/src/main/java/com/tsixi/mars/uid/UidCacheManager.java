/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.uid;

import com.tsixi.mars.cluster.ClusterClient;
import com.tsixi.mars.cluster.ClusterSystem;
import com.tsixi.mars.curator.NodeType;
import com.tsixi.mars.service.UidService;

/**
 * uid 缓存实现
 * <p>
 * Created on 2017/3/8.
 *
 * @author Alan
 * @since 1.0
 */
public class UidCacheManager {
    private UidCache[] uidCaches;
    private UidService uidService;
    private boolean useCache;
    private ClusterSystem clusterSystem;

    public UidCacheManager(ClusterSystem clusterSystem, boolean useCache) {
        this.useCache = useCache;
        this.uidCaches = new UidCache[UidTypeEnum.values().length];
        this.clusterSystem = clusterSystem;
    }

    public long getUid(UidTypeEnum key) {
        if (uidService == null) {
            ClusterClient clusterClient = clusterSystem.getByNodeType(NodeType.CENTER);
            uidService = clusterClient.getRpcClient().create(UidService.class);
        }
        if (!useCache) {
            return uidService.getAndUpdateUid(key, 1);
        }
        synchronized (key) {
            UidCache uidCreator = uidCaches[key.ordinal()];
            int num = key.getCacheNum();
            // 如果缓存对象还没有被初始化或者缓存已经被使用完
            if (uidCreator == null || uidCreator.currentUid == uidCreator.maxUid) {
                long uid = uidService.getAndUpdateUid(key, num);
                uidCreator = new UidCache(uid - num, uid);
                uidCaches[key.ordinal()] = uidCreator;
            }
            return ++uidCreator.currentUid;
        }
    }
}
