package com.workbuddy.cluster.model;


import lombok.Data;

@Data
public class NodeInfo {
    private String nodeName;
    private String nodeHostName;
    private String nodeIp;
    private String tailScaleIp;
    private String macAddress;


    // CPU
    private int nodeCores;
    private double nodeLoad;

    // Memory
    private long nodeMemoryMb;
    private double nodeMemoryUsage;  // %

    // Disk
    private long nodeDiskGb;
    private double nodeDiskUsage;    // %

    // System
    private long nodeUptimeSeconds;

    private NodeState state;
    private long lastSeen;
}
