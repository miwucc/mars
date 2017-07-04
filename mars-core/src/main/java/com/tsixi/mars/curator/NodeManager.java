/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package com.tsixi.mars.curator;

import com.alibaba.fastjson.JSON;
import com.tsixi.mars.config.NodeConfig;
import com.tsixi.mars.data.MarsConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

import static com.tsixi.mars.data.MarsConst.SEPARATOR;

/**
 * Created on 2017/3/6.
 *
 * @author alan
 * @since 1.0
 */
@Component
public class NodeManager implements MarsCuratorListener {
    Logger log = LoggerFactory.getLogger(getClass());

    private NodeConfig nodeConfig;

    private MarsCurator marsCurator;

    @Autowired
    public NodeManager(NodeConfig nodeConfig, MarsCurator marsCurator) {
        this.nodeConfig = nodeConfig;
        this.marsCurator = marsCurator;
    }

    @Override
    public void marsCuratorRefreshed(MarsCurator marsCurator) {
        try {
            StringBuilder sb = new StringBuilder(SEPARATOR);
            sb.append(nodeConfig.getParentPath()).append(SEPARATOR)
                    .append(nodeConfig.getType()).append(SEPARATOR)
                    .append(nodeConfig.getName());
            String path = sb.toString();
            log.info("node register,path is {}", path);
            String nc = JSON.toJSONString(nodeConfig);
            marsCurator.addPath(path, nc.getBytes("UTF-8"), false);
        } catch (UnsupportedEncodingException e) {
            log.warn("node register fail.", e);
        }
    }

    public MarsNode getMarNode(NodeType nodeType) {
        String nodePath = MarsConst.SEPARATOR + nodeConfig.getParentPath()
                + MarsConst.SEPARATOR + nodeType;
        return marsCurator.getMarsNode(nodePath);
    }
}
