package com.workbuddy.node.events;

import com.workbuddy.node.component.SystemInfoProvider;
import com.workbuddy.node.model.CommandMessage;
import com.workbuddy.node.model.NodeInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
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
            public Type getPayloadType(@NonNull StompHeaders headers) {
                return headers.getContentType().includes(MediaType.APPLICATION_JSON)
                        ? CommandMessage.class
                        : String.class;
            }

            @Override
            public void handleFrame(@NonNull StompHeaders headers, @Nullable Object payload) {
                CommandMessage command = (CommandMessage) payload;
                log.info("Received command: {}", command);
                if ((command != null ? command.getCommand() : null) != null) {
                    switch (command.getCommand()){
                        case POWER_OFF -> CompletableFuture.runAsync(() -> powerOff());
                        case RESTART -> CompletableFuture.runAsync(() -> restart());
                        default -> log.info("Unknown command: {}" , command);
                    }
                }
            }
        });
    }

    private void powerOff() {
        execute("sudo", "/usr/sbin/poweroff");
    }

    private void restart() {
        execute("sudo", "/usr/sbin/reboot");
    }

    private void execute(String... cmd) {
        try {
            new ProcessBuilder(cmd).start();
        } catch (Exception e) {
            System.err.println("Command failed");
            log.warn("Command Error: {}",e.getMessage());
        }
    }
}
