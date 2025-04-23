package ch.uzh.ifi.hase.soprafs24.security;

import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.HandshakeInterceptor;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

@Component
public class TokenHandshakeInterceptor implements HandshakeInterceptor {

    private final UserRepository userRepo;

    public TokenHandshakeInterceptor(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            org.springframework.web.socket.WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        // Only Servlet requests can have query params
        if (!(request instanceof ServletServerHttpRequest servletReq)) {
            return false;
        }
        String token = servletReq.getServletRequest().getParameter("token");
        if (token == null || token.isBlank()) {
            return false;
        }

        User user = userRepo.findByToken(token);
        if (user == null || user.getStatus() != UserStatus.ONLINE) {
            return false;
        }
        // stash a Principal directly
        attributes.put("userPrincipal", new StompPrincipal(user.getUserId()));
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            org.springframework.web.socket.WebSocketHandler wsHandler,
            Exception exception) {
    }
}