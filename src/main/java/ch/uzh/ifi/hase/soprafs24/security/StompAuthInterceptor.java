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
public class StompAuthInterceptor implements ChannelInterceptor {
  @Autowired private UserService userService;

  @Override
  public Message<?> preSend(Message<?> msg, MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(msg);
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = accessor.getFirstNativeHeader("Authorization");
      if (token==null || userService.getUserByToken(token)==null) {
        throw new IllegalArgumentException("Invalid or missing token");
      }

    }
    return msg;
  }
}