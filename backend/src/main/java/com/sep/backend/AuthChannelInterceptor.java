package com.sep.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class AuthChannelInterceptor implements ChannelInterceptor {
    private final WebSocketAuthService permissionService;

    public AuthChannelInterceptor(WebSocketAuthService permissionService) {
        this.permissionService = permissionService;
    }


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            throw new IllegalStateException("No StompHeaderAccessor found");
        }

        log.debug("Intercepting STOMP message.");

        accessor.toNativeHeaderMap().forEach((key, valueList) -> log.debug("Native Header: {} = {}", key, valueList));

        accessor.getMessageHeaders().forEach((key, value) -> log.debug("General Header: {} = {}", key, value));

        resolveStompCommand(accessor);

        return message;
    }

    private void resolveStompCommand(StompHeaderAccessor accessor) {
        final var command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            log.debug("Resolving CONNECT command.");
            permissionService.authorizeUser(accessor);
        } else if (StompCommand.SUBSCRIBE.equals(command)) {
            log.debug("Resolving SUBSCRIBE command.");
            permissionService.subscribeUser(accessor);
        }
    }

}
