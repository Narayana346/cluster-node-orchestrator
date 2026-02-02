package com.workbuddy.node.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeInfo {

    // Identity
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
}

