package ch.uzh.ifi.hase.soprafs24.security;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.http.HttpStatus;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    @Autowired
    public AuthenticationInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        // Exclude login endpoint from authentication check
        if (requestURI.equals("/login")){
        return true;
        }

        if (requestURI.equals("/users")) {
            if (method.equalsIgnoreCase("POST")) {
                return true; // Allow registration without authentication
            }
        }

        // Token aus dem Header holen
        String token = request.getHeader("Authorization");

        // Falls kein Token vorhanden ist, Zugriff verweigern
        if (token == null || token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No token provided");

        }

        // Benutzer anhand des Tokens suchen
        User user = userRepository.findByToken(token);
        if (user == null || user.getStatus() != UserStatus.ONLINE) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token, please login again");
        }

        return true; // Erlaubt die Anfrage
    }
}