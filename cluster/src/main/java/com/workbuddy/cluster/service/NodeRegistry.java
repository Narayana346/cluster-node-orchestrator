package com.workbuddy.cluster.service;

import com.workbuddy.cluster.model.NodeInfo;
import com.workbuddy.cluster.model.NodeState;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NodeRegistry {
    private final Map<String, NodeInfo> nodes = new ConcurrentHashMap<>();

    public void markOnline(NodeInfo nodeInfo) {
        NodeInfo info = nodes.getOrDefault(nodeInfo.getNodeName(), new NodeInfo());
        info.setNodeName(nodeInfo.getNodeName());
        info.setNodeHostName(nodeInfo.getNodeHostName());
        info.setNodeIp(nodeInfo.getNodeIp());
        info.setTailScaleIp(nodeInfo.getTailScaleIp());
        info.setMacAddress(nodeInfo.getMacAddress());
        info.setNodeCores(nodeInfo.getNodeCores());
        info.setNodeLoad(nodeInfo.getNodeLoad());
        info.setNodeMemoryMb(nodeInfo.getNodeMemoryMb());
        info.setNodeMemoryUsage(nodeInfo.getNodeMemoryUsage());
        info.setNodeDiskGb(nodeInfo.getNodeDiskGb());
        info.setNodeDiskUsage(nodeInfo.getNodeDiskUsage());
        info.setNodeUptimeSeconds(nodeInfo.getNodeUptimeSeconds());
        info.setState(NodeState.UP);
        info.setLastSeen(System.currentTimeMillis());
        nodes.put(nodeInfo.getNodeName(), info);
    }

    public void markOffline(String nodeName) {
        if (nodes.containsKey(nodeName)) {
            nodes.get(nodeName).setState(NodeState.DOWN);
        }
    }

    public Map<String, NodeInfo> all() {
        return nodes;
    }
}
