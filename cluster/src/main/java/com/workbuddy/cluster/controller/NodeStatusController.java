package com.workbuddy.cluster.controller;


import com.workbuddy.cluster.service.NodeRegistry;
import com.workbuddy.cluster.service.WOLService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cluster")
@RequiredArgsConstructor
public class NodeStatusController {

    private final NodeRegistry registry;
    private final WOLService wolService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/nodes")
    public Object allNodes() {
        return registry.all();
    }

    @PostMapping("/{node}/on")
    public String powerOn(@PathVariable String node, @RequestParam String mac) {
        wolService.sendMagicPacket(mac);
        return "Power ON sent to " + node;
    }

    @PostMapping("/{node}/off")
    public String powerOff(@PathVariable String node){
        messagingTemplate.convertAndSend("/topic/commands/" + node, "POWER_OFF");
        return "Power off sent to " + node;
    }
}

