package com.workbuddy.node.component;

import com.workbuddy.node.events.NodeEvents;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Component
@Slf4j
public class WorkerWebSocketClient {

    private final String masterUrl;
    private final String nodeName;
    private final WebSocketStompClient stompClient;
    private final NodeEvents nodeEvents;

    public WorkerWebSocketClient(WebSocketStompClient stompClient,
                                 @Value("${worker.node-name}") String nodeName,
                                 @Value("${worker.master-url}") String masterUrl,
                                 NodeEvents nodeEvents) {
        this.masterUrl = masterUrl;
        this.stompClient = stompClient;
        this.nodeName = nodeName;
        this.nodeEvents = nodeEvents;
    }

    @PostConstruct
    public void connect() {
        try {
            // 👉 Use async connect
            stompClient.connectAsync(masterUrl, new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(@NonNull StompSession session, @NonNull StompHeaders headers) {
                    log.info("{} Connected to master", nodeName);
                    nodeEvents.nodeOnline(session,headers, nodeName);
                    nodeEvents.subscribe(session,headers, nodeName);

                }

                @Override
                public void handleTransportError(@NonNull StompSession session, @NonNull Throwable exception) {
                    log.warn("WebSocket error: {}", exception.getMessage());
                    reconnect();
                }
            });

        } catch (Exception e) {
            log.warn("Connection failed: {}", e.getMessage());
            reconnect();
        }
    }

    private void reconnect() {
        try {
            Thread.sleep(5000); // wait 5 seconds
        } catch (InterruptedException ignored) {}

        log.info("Reconnecting...");
        connect();
    }
}


