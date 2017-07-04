/**
 * Copyright Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 * <p>
 * 2017年3月2日
 */
package com.tsixi.mars.curator;

import com.alibaba.fastjson.JSON;
import com.tsixi.mars.config.ZookeeperConfig;
import com.tsixi.mars.curator.MarsNodeListener.NodeChangeType;
import com.tsixi.mars.data.MarsConst;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * zookeeper 监视器管理类
 *
 * @author Alan
 * @scene 1.0
 */
@Order(2)
@Component
public class MarsCurator implements ApplicationListener<ContextRefreshedEvent>,
        ApplicationRunner, TreeCacheListener {

    private Logger log = LoggerFactory.getLogger(getClass());

    private CuratorFramework client = null;

    private final ZookeeperConfig zkConfig;

    private String rootPath;

    private MarsNode marsRootNode;

    private TreeCache treeCache;

    private Set<MarsCuratorListener> listeners = new CopyOnWriteArraySet<>();

    private Map<String, Set<MarsNodeListener>> marsNodeListeners = new HashMap<>();

    @Autowired
    public MarsCurator(ZookeeperConfig zkConfig) {
        this.zkConfig = zkConfig;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, MarsCuratorListener> tmpMap = context
                .getBeansOfType(MarsCuratorListener.class);
        if (tmpMap != null && !tmpMap.isEmpty()) {
            listeners = new CopyOnWriteArraySet<>(tmpMap.values());
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            log.debug("mars curator init. ");
            ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(
                    zkConfig.getBaseSleepTimeMs(), zkConfig.getMaxRetries());
            client = CuratorFrameworkFactory.newClient(zkConfig.getConnects(),
                    5000, 3000, retryPolicy);
            client.start();
            initRootPath();
            cacheMarsNode();
        } catch (Exception e) {
            log.warn("mars curator init fail...", e);
        }
    }

    public void stop() {
        try {
            if (treeCache != null) {
                treeCache.close();
            }
            if (client != null) {
                client.close();
            }
        } catch (Exception e) {
            log.warn("mars curator stop fail.", e);
        }
    }

    public void restart() {
        try {
            stop();
            run(null);
        } catch (Exception e) {
            log.warn("restart fail.", e);
        }
    }

    public CuratorFramework getClient() {
        return client;
    }

    /**
     * 锁服务，获得一个指定路径的锁
     *
     * @param lockPath
     * @return
     */
    public InterProcessMutex getLock(String lockPath) {
        return new InterProcessMutex(client, lockPath);
    }

    public boolean addPath(String path, byte[] payload, boolean persistent) {
        path = mkPath(path);
        log.warn("add path {}", path);
        try {
            if (!checkExists(path)) {
                client.create()
                        .creatingParentsIfNeeded().withMode(persistent
                        ? CreateMode.PERSISTENT : CreateMode.EPHEMERAL)
                        .forPath(path, payload);
                return true;
            }
        } catch (Exception e) {
            log.warn("add path fail.path is {}", path, e);
        }
        return false;
    }

    public void addMarsNodeListener(String path, MarsNodeListener listener) {
        log.info("add mars node listener.path={},listener={}", path, listener);
        Set<MarsNodeListener> set = marsNodeListeners.computeIfAbsent(path, e -> new HashSet<>());
        set.add(listener);
    }

    public void removeMarsNodeListener(String path, MarsNodeListener listener) {
        log.info("remove mars node listener.path={},listener={}", path, listener);
        Set<MarsNodeListener> set = marsNodeListeners.computeIfAbsent(path, e -> new HashSet<>());
        set.remove(listener);
    }

    private void notifyMarsNodeListener(NodeChangeType nodeChangeType, MarsNode marsNode) {
        String path = marsNode.getNodePath();
        log.debug("notify mars node listeners.path={},nodeChangeType={}", path, nodeChangeType);
        Set<MarsNodeListener> set = marsNodeListeners.computeIfAbsent(path, e -> new HashSet<>());
        set.forEach(listener -> {
            listener.nodeChange(nodeChangeType, marsNode);
            log.info("notify mars node listeners.path={},nodeChangeType={},listener={}", path, nodeChangeType, listener);
        });
    }


    public MarsNode getMarsNode(String path) {
        path = mkPath(path);
        return marsRootNode.getChildren(path, true);
    }

    private MarsNode addMarsNode(String path, String data) {
        MarsNode marsNode = new MarsNode(path, data);
        if (path.equals(rootPath)) {
            marsRootNode = marsNode;
        } else {
            marsRootNode.addChildren(marsNode);
        }
        notifyMarsNodeListener(NodeChangeType.NODE_ADD, marsNode);
        return marsNode;
    }

    private MarsNode removeMarsNode(String path) {
        MarsNode marsNode = marsRootNode.removeChildren(path);
        if (marsNode != null) {
            notifyMarsNodeListener(NodeChangeType.NODE_REMOVE, marsNode);
        }
        return marsNode;
    }

    private void cacheMarsNode() throws Exception {
        treeCache = new TreeCache(client, rootPath);
        treeCache.getListenable().addListener(this);
        treeCache.start();
    }

    private boolean checkExists(String path) throws Exception {
        Stat stat = client.checkExists().forPath(path);
        return stat != null;
    }

    private String mkPath(String path) {
        return rootPath + path;
    }

    private void initRootPath() {
        try {
            rootPath = MarsConst.SEPARATOR + zkConfig.getMarsRoot();
            if (!checkExists(rootPath)) {
                log.debug("mars root path {} not exist.", rootPath);
                client.create().forPath(rootPath);
            }
        } catch (Exception e) {
            log.warn("mars root path init fail...", e);
        }
    }

    private void notifyRefreshed() {
        listeners.forEach(listener -> {
            try {
                listener.marsCuratorRefreshed(this);
            } catch (Exception e) {
                log.warn("notify refreshed error.", e);
            }
        });
    }

    @Override
    public void childEvent(CuratorFramework client, TreeCacheEvent event)
            throws Exception {
        TreeCacheEvent.Type type = event.getType();
        ChildData childData = event.getData();
        if (childData == null) {
            log.debug("No data in event[" + event + "]");
            switch (type) {
                case CONNECTION_LOST:
                    if (client.getZookeeperClient().blockUntilConnectedOrTimedOut()) {
//            case CONNECTION_SUSPENDED:
                        restart();
                    }
                    break;
                case CONNECTION_RECONNECTED:
                case INITIALIZED:
                    notifyRefreshed();
                    break;
            }
        } else {
            byte[] byteData = childData.getData();
            if (byteData == null) {
                byteData = new byte[0];
            }
            String data = new String(byteData, "UTF-8");
            String strStat = JSON.toJSONString(childData.getStat());
            String path = childData.getPath();
            log.debug("Receive event: " + "type=[" + type + "], path=" + path
                    + ", data=" + data + ", stat=" + strStat);
            switch (type) {
                case NODE_ADDED:
                case NODE_UPDATED:
                    addMarsNode(path, data);
                    break;
                case NODE_REMOVED:
                    removeMarsNode(path);
                    break;
            }
        }
    }
}
