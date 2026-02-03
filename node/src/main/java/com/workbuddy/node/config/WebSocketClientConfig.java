package com.workbuddy.node.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Configuration
public class WebSocketClientConfig {

    @Bean
    public WebSocketStompClient stompClient(StandardWebSocketClient standardWebSocketClient,
                                            JacksonJsonMessageConverter jacksonJsonMessageConverter){

        WebSocketStompClient client =  new WebSocketStompClient(standardWebSocketClient);
        client.setMessageConverter(jacksonJsonMessageConverter);
        return client;
    }

    @Bean
    public StandardWebSocketClient standardWebSocketClient(){
        return new StandardWebSocketClient();
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter(){
        return new JacksonJsonMessageConverter();
    }
}
