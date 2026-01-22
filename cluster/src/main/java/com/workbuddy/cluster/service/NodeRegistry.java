package com.workbuddy.cluster.service;

import com.workbuddy.cluster.model.NodeInfo;
import com.workbuddy.cluster.model.NodeState;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NodeRegistry {
    private final Map<String, NodeInfo> nodes = new ConcurrentHashMap<>();

    public void markOnline(String nodeName) {
        NodeInfo info = nodes.getOrDefault(nodeName, new NodeInfo());
        info.setNodeName(nodeName);
        info.setState(NodeState.UP);
        info.setLastSeen(System.currentTimeMillis());
        nodes.put(nodeName, info);
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
