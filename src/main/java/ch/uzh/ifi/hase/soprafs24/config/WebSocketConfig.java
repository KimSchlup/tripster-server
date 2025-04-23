package ch.uzh.ifi.hase.soprafs24.config;

import java.security.Principal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import ch.uzh.ifi.hase.soprafs24.security.StompPrincipal;
import ch.uzh.ifi.hase.soprafs24.security.TokenHandshakeInterceptor;
import ch.uzh.ifi.hase.soprafs24.service.RoadtripService;

import org.springframework.messaging.Message;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final TokenHandshakeInterceptor tokenInterceptor;
    private final RoadtripService roadtripService;

    public WebSocketConfig(TokenHandshakeInterceptor tokenInterceptor,
            RoadtripService roadtripService) {
        this.tokenInterceptor = tokenInterceptor;
        this.roadtripService = roadtripService;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")
                .addInterceptors(tokenInterceptor)
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(
                            ServerHttpRequest request,
                            org.springframework.web.socket.WebSocketHandler wsHandler,
                            Map<String, Object> attributes) {
                        // pull the principal we stored in beforeHandshake:
                        return (Principal) attributes.get("userPrincipal");
                    }
                })
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /*
     * configureMessageBroker is responsible for two things:
     * 
     * 1. Creates in-memory message broker with destination(s) for
     * sending and receiving messages.
     * Two destinations are defined: /topic & /app
     * Destinations prefixed by /topic are for carried to all subscribed clients
     * Destinations prefixed by /queue are for point-to-point communication
     * 
     * 2. Defines the prefix app that is used to filter destinations handled by
     * methods annotated with @MessageMapping which you will implement in a
     * controller. The controller, after processing the message, will send it to the
     * broker.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // destination prefix for clients to subscribe
        config.setApplicationDestinationPrefixes("/app"); // prefix for sending messages
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor header = StompHeaderAccessor.wrap(message);

                if (StompCommand.SUBSCRIBE.equals(header.getCommand())) {
                    Principal principal = header.getUser();
                    if (!(principal instanceof StompPrincipal)) {
                        throw new MessagingException("Not authenticated");
                    }
                    Matcher m = Pattern.compile("/topic/roadtrips/(\\d+)/pois")
                            .matcher(header.getDestination());
                    if (m.matches()) {
                        Long roadtripId = Long.valueOf(m.group(1));
                        Long userId = ((StompPrincipal) principal).getUserId();

                        if (!roadtripService.isMember(roadtripId, userId)) {
                            throw new MessagingException("Forbidden");
                        }
                    }
                }
                return message;
            }
        });
    }
}