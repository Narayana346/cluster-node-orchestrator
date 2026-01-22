package com.workbuddy.cluster.model;


import lombok.Data;

@Data
public class NodeInfo {
    private String nodeName;
    private NodeState state;
    private long lastSeen;
}
