package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.UserEmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserEmergencyContactRepository extends JpaRepository<UserEmergencyContact, Long> {
    List<UserEmergencyContact> findByUser_UserId(Long userId);
}
