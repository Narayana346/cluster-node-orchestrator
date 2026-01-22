package com.workbuddy.cluster.events;

import com.workbuddy.cluster.service.NodeRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

@Component
@RequiredArgsConstructor
public class MasterWebSocketEvents implements ApplicationListener<SessionDisconnectEvent> {

    private final NodeRegistry registry;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String nodeName = accessor.getSessionAttributes() != null ?
                (String) accessor.getSessionAttributes().get("node") : null;

        if (nodeName != null) {
            registry.markOffline(nodeName);
            System.out.println("Node disconnected: " + nodeName);
        }
    }
}
