package com.workbuddy.node.component;

import com.workbuddy.node.events.NodeEvents;
import jakarta.annotation.PostConstruct;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;

@Component
public class WorkerWebSocketClient {

    private final String masterUrl;
    private final String nodeName;
    private final NodeEvents nodeEvents;
    private final WebSocketStompClient client;

    public WorkerWebSocketClient(
            @Value("${worker.master-url}") String masterUrl,
            @Value("${worker.node-name}") String nodeName,
            NodeEvents nodeEvents
    ) {
        this.masterUrl = masterUrl;
        this.nodeName = nodeName;
        this.nodeEvents = nodeEvents;
        this.client = new WebSocketStompClient(new StandardWebSocketClient());
    }

    @PostConstruct
    public void connect() {
        try {
            // 👉 Modern JSON converter (Spring 7.x compatible)
            client.setMessageConverter(new JacksonJsonMessageConverter());

            // 👉 Use async connect
            client.connectAsync(masterUrl, new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(StompSession session, StompHeaders headers) {
                    System.out.println("Connected to master");
                    nodeEvents.nodeOnline(session,headers, nodeName);
                    nodeEvents.subscribe(session,headers, nodeName);

                }

                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    System.out.println("WebSocket error: " + exception.getMessage());
                    reconnect();
                }
            });

        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
            reconnect();
        }
    }

    private void reconnect() {
        try {
            Thread.sleep(5000); // wait 5 seconds
        } catch (InterruptedException ignored) {}

        System.out.println("Reconnecting...");
        connect();
    }
}


