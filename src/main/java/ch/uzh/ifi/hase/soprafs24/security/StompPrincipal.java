package ch.uzh.ifi.hase.soprafs24.security;

import java.security.Principal;

/**
 * A minimal Principal that carries a user’s ID.
 */
public class StompPrincipal implements Principal {
    private final Long userId;

    public StompPrincipal(Long userId) {
        this.userId = userId;
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }

    public Long getUserId() {
        return userId;
    }
}
