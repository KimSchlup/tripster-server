package ch.uzh.ifi.hase.soprafs24.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripMemberRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository;
import ch.uzh.ifi.hase.soprafs24.service.RoadtripService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

@Component
public class WebSocketSubscriptionInterceptor implements ChannelInterceptor {

    @Autowired
    private UserService userService;
    @Autowired
    private RoadtripRepository roadtripRepository;
    @Autowired
    private RoadtripMemberRepository roadtripMemberRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        // 1) On CONNECT: grab the token from the STOMP headers, validate it,
        // and stash it into the session attributes for later SUBSCRIBE calls.
        if (StompCommand.CONNECT.equals(command)) {
            String token = accessor.getFirstNativeHeader("Authorization");
            User user = (token == null)
                    ? null
                    : userService.getUserByToken(token);
            if (user == null) {
                throw new IllegalArgumentException("Invalid or missing session token");
            }
            // put it in the WebSocket session
            accessor.getSessionAttributes().put("sessionToken", token);
        }

        // 2) On SUBSCRIBE: retrieve the previously‐stored token, re‐validate,
        // then enforce roadtrip‑membership as before.
        if (StompCommand.SUBSCRIBE.equals(command)) {
            String token = (String) accessor.getSessionAttributes().get("sessionToken");
            if (token == null) {
                throw new IllegalArgumentException("Not authenticated");
            }
            User user = userService.getUserByToken(token);

            Long roadtripId = extractRoadtripId(accessor.getDestination());
            Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                    .orElseThrow(() -> new IllegalArgumentException("Roadtrip not found"));

            RoadtripMember member = roadtripMemberRepository
                    .findByUserAndRoadtrip(user, roadtrip);

            boolean isMember = member != null
                    && member.getInvitationStatus() == InvitationStatus.ACCEPTED;
            if (!isMember) {
                throw new IllegalArgumentException("Unauthorized to subscribe to this roadtrip");
            }
        }

        return message;
    }

    private Long extractRoadtripId(String destination) {
        try {
            String[] parts = destination.split("/");
            return Long.parseLong(parts[2]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid destination: " + destination);
        }
    }
}