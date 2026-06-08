package com.example.Second_hand.trading.platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.example.Second_hand.trading.platform.service.JwtService;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	private final JwtService jwtService;

	public WebSocketConfig(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic", "/queue");
		config.setApplicationDestinationPrefixes("/app");
		config.setUserDestinationPrefix("/user");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
				.setAllowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")
				.withSockJS();
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				if (accessor == null || accessor.getCommand() == null) {
					return message;
				}
				if (StompCommand.CONNECT.equals(accessor.getCommand())) {
					JwtService.JwtClaims claims = jwtService.requireAuthorization(accessor.getFirstNativeHeader("Authorization"));
					if (!claims.isUser()) {
						throw new MessagingException("WebSocket only supports user tokens");
					}
					accessor.setUser(() -> String.valueOf(claims.id()));
					return message;
				}
				if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
					ensureAuthenticated(accessor);
					String destination = accessor.getDestination();
					if (!"/user/queue/notifications".equals(destination)
							&& !"/user/queue/messages".equals(destination)
							&& !"/topic/broadcast".equals(destination)) {
						throw new MessagingException("Subscription destination is not allowed");
					}
					return message;
				}
				if (StompCommand.SEND.equals(accessor.getCommand())) {
					throw new MessagingException("Client WebSocket writes are disabled");
				}
				return message;
			}
		});
	}

	private void ensureAuthenticated(StompHeaderAccessor accessor) {
		if (accessor.getUser() == null || !StringUtils.hasText(accessor.getUser().getName())) {
			throw new MessagingException("WebSocket authentication is required");
		}
	}
}
