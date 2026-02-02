package com.workbuddy.cluster.controller;

import com.workbuddy.cluster.model.NodeInfo;
import com.workbuddy.cluster.service.NodeRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NodeWebSocketController {
    private final NodeRegistry nodeRegistry;

    @MessageMapping("/node/online")
    private void nodeOnline(NodeInfo nodeInfo){
        nodeRegistry.markOnline(nodeInfo);
        System.out.println("node online: "+ nodeInfo.getNodeName());
    }
}
