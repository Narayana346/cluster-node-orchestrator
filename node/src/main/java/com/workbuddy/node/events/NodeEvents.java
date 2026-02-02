package com.workbuddy.node.events;

import com.workbuddy.node.component.SystemInfoProvider;
import com.workbuddy.node.model.NodeInfo;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;

@Service
@RequiredArgsConstructor
public class NodeEvents {
    private static final String NODE_ONLINE_PATH = "/app/node/online";
    private static final String NODE_COMMAND_PATH = "/topic/commands/";

    private final SystemInfoProvider systemInfoProvider;


    public void nodeOnline(StompSession session, StompHeaders headers, String nodeName) {
        NodeInfo nodeInfo = systemInfoProvider.buildNodeInfo(nodeName);
        session.send(NODE_ONLINE_PATH, nodeInfo);
    }

    public void subscribe(StompSession session, StompHeaders headers, String nodeName) {
        session.subscribe(NODE_COMMAND_PATH + nodeName, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, @Nullable Object payload) {
                String command = (String) payload;
                System.out.println("Received command: " + command);
                switch (command){
                    case "POWER_OFF":
                        powerOff();
                        break;
                    default:
                        System.out.println("Unknown command: " + command);
                        break;
                }
            }
        });
    }

    private void powerOff(){
        try {
            Runtime.getRuntime().exec("sudo /usr/sbin/poweroff");
        } catch (Exception e) {
            System.out.println("Power off failed: " + e.getMessage());
        }
    }
}
