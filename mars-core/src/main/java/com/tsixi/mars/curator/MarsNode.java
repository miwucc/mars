/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.curator;

import java.util.*;

/**
 * <p>节点</p>
 * <p>
 * Created by Alan (mingweiyang@foxmail.com) on 2017/3/7.
 *
 * @since 1.0
 */
public class MarsNode {
    /**
     * 节点路径
     */
    private String nodePath;
    /**
     * 节点数据
     */
    private String nodeData;
    /**
     * 子节点
     */
    private Map<String, MarsNode> childrenNodes = new HashMap<>();

    private Random random = new Random();

    public MarsNode(String nodePath, String nodeData) {
        this.nodePath = nodePath;
        this.nodeData = nodeData;
    }

    public MarsNode addChildren(MarsNode marsNode) {
        MarsNode mn = getChildren(marsNode.getNodePath(), false);
        // 找不到或者是本层目录的节点，则直接更新
        if (mn == null || mn.getNodePath().equals(marsNode.getNodePath())) {
            return childrenNodes.put(marsNode.getNodePath(), marsNode);
        } else {
            return mn.addChildren(marsNode);
        }
    }

    public MarsNode removeChildren(String path) {
        if (childrenNodes.containsKey(path)) {
            return childrenNodes.remove(path);
        } else {
            MarsNode marsNode = getChildren(path, true);
            if (marsNode != null) {
                return marsNode.removeChildren(path);
            }
        }
        return null;
    }

    public MarsNode getChildren(String path, boolean rc) {
        if (!childrenNodes.isEmpty()) {
            for (MarsNode marsNode : childrenNodes.values()) {
                if (path.equals(marsNode.getNodePath())) {
                    return marsNode;
                }
                if (path.startsWith(marsNode.getNodePath())) {
                    if (rc) {
                        return marsNode.getChildren(path, rc);
                    }
                    return marsNode;
                }
            }
        }
        return null;
    }

    /**
     * 获取本节点下的所有子节点
     *
     * @return
     */
    public List<MarsNode> getAllChildren() {
        return new ArrayList<>(childrenNodes.values());
    }

    /**
     * 从节点中随机选择一个子节点，如果节点不包含子节点返回null
     *
     * @return
     */
    public MarsNode randomOneMarsNode() {
        if (childrenNodes.isEmpty()) {
            return null;
        }
        List<MarsNode> childrens = getAllChildren();
        int p = random.nextInt(childrens.size());
        return childrens.get(p);
    }

    public boolean hasChildren() {
        return !childrenNodes.isEmpty();
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }

    public String getNodeData() {
        return nodeData;
    }
}
