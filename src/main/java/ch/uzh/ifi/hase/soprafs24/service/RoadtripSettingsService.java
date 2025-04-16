package ch.uzh.ifi.hase.soprafs24.service;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.constant.BasemapType;
import ch.uzh.ifi.hase.soprafs24.constant.DecisionProcess;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Roadtrip;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMember;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripMemberPK;
import ch.uzh.ifi.hase.soprafs24.entity.RoadtripSettings;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripMemberRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripRepository;
import ch.uzh.ifi.hase.soprafs24.repository.RoadtripSettingsRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class RoadtripSettingsService {

    @Autowired
    private final RoadtripSettingsRepository roadtripSettingsRepository;
    private final RoadtripRepository roadtripRepository;
    private final RoadtripMemberRepository roadtripMemberRepository;

    public RoadtripSettingsService(
            @Qualifier("roadtripSettingsRepository") RoadtripSettingsRepository roadtripSettingsRepository,
            @Qualifier("roadtripRepository") RoadtripRepository roadtripRepository,
            @Qualifier("roadtripMemberRepository") RoadtripMemberRepository roadtripMemberRepository) {
        this.roadtripMemberRepository = roadtripMemberRepository;
        this.roadtripRepository = roadtripRepository;
        this.roadtripSettingsRepository = roadtripSettingsRepository;
    }

    public RoadtripSettings getRoadtripSettingsById(Long roadtripId, User user) {

        // Check if roadtrip exists
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));

        // Check if user is owner or member
        boolean isOwner = Objects.equals(roadtrip.getOwner(), user);
        RoadtripMember roadtripMember = roadtripMemberRepository.findByUserAndRoadtrip(user, roadtrip);
        boolean isMember = roadtripMember != null && roadtripMember.getInvitationStatus() == InvitationStatus.ACCEPTED;

        if (!isOwner && !isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a member of this roadtrip");
        }

        // Fetch roadtrip settings
        RoadtripSettings roadtripSettings = roadtripSettingsRepository.findByRoadtrip_RoadtripId(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip settings not found"));

        return roadtripSettings;

    }

    /**
     * Update the RoadtripSettings object with the given id.
     *
     * @param roadtripSettingsId the id of the RoadtripSettings object to update
     * @param roadtripSettings   the new RoadtripSettings object
     * @return the updated RoadtripSettings object
     */
    public void updateRoadtripSettingsById(Long roadtripId, RoadtripSettings updatedRoadtripSettings, User user) {

        // Check if roadtrip exists
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));

        // Check if user is owner
        boolean isOwner = Objects.equals(roadtrip.getOwner(), user);

        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the roadtrip owner can access the settings");
        }

        // Fetch the existing RoadtripSettings object
        RoadtripSettings roadtripSettings = roadtripSettingsRepository.findByRoadtrip_RoadtripId(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip settings not found"));

        if (updatedRoadtripSettings.getBasemapType() != null) {
            roadtripSettings.setBasemapType(updatedRoadtripSettings.getBasemapType());
        }
        if (updatedRoadtripSettings.getDecisionProcess() != null) {
            roadtripSettings.setDecisionProcess(updatedRoadtripSettings.getDecisionProcess());
        }
        if (updatedRoadtripSettings.getBoundingBox() != null) {
            roadtripSettings.setBoundingBox(updatedRoadtripSettings.getBoundingBox());
        }
        if (updatedRoadtripSettings.getStartDate() != null) {
            roadtripSettings.setStartDate(updatedRoadtripSettings.getStartDate());
        }
        if (updatedRoadtripSettings.getEndDate() != null) {
            roadtripSettings.setEndDate(updatedRoadtripSettings.getEndDate());
        }

        this.roadtripSettingsRepository.save(roadtripSettings);
        roadtripSettingsRepository.flush();
    }

    /**
     * Create a new RoadtripSettings object with default values.
     * There is no POST RoadtripSettings endpoint, so this method
     * is used by RoadtripService to create the settings when a new roadtrip is
     * created.
     *
     * @param roadtrip the roadtrip to associate with the settings
     * @return the created RoadtripSettings object
     */
    public RoadtripSettings createRoadtripSettings(Roadtrip roadtrip) {

        RoadtripSettings roadtripSettings = new RoadtripSettings();
        roadtripSettings.setRoadtrip(roadtrip);
        roadtripSettings.setBasemapType(BasemapType.OPEN_STREET_MAP);
        roadtripSettings.setDecisionProcess(DecisionProcess.MAJORITY);
        roadtripSettings.setStartDate(LocalDate.now());
        roadtripSettings.setEndDate(LocalDate.now().plusDays(7));

        return roadtripSettingsRepository.save(roadtripSettings);
    }

}
