package ch.uzh.ifi.hase.soprafs24.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
import org.springframework.http.ResponseEntity;

import ch.uzh.ifi.hase.soprafs24.service.GoogleCloudStorageService;

@Service
@Transactional
public class RoadtripSettingsService {

    @Autowired
    private final RoadtripSettingsRepository roadtripSettingsRepository;
    private final RoadtripRepository roadtripRepository;
    private final RoadtripMemberRepository roadtripMemberRepository;
    private final GoogleCloudStorageService storageService;

    public RoadtripSettingsService(
            @Qualifier("roadtripSettingsRepository") RoadtripSettingsRepository roadtripSettingsRepository,
            @Qualifier("roadtripRepository") RoadtripRepository roadtripRepository,
            @Qualifier("roadtripMemberRepository") RoadtripMemberRepository roadtripMemberRepository,
            GoogleCloudStorageService storageService) {
        this.roadtripMemberRepository = roadtripMemberRepository;
        this.roadtripRepository = roadtripRepository;
        this.roadtripSettingsRepository = roadtripSettingsRepository;
        this.storageService = storageService;
    }

    public RoadtripSettings getRoadtripSettingsById(Long roadtripId, User user) {

        // Check if roadtrip exists
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));

        // Check if user is owner or member
        boolean isOwner = Objects.equals(roadtrip.getOwner().getUserId(), user.getUserId());
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
        try {
            // Check if roadtrip exists
            Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));

            // Check if user is owner
            boolean isOwner = Objects.equals(roadtrip.getOwner().getUserId(), user.getUserId());

            if (!isOwner) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Only the roadtrip owner can access the settings");
            }

            // Fetch the existing RoadtripSettings object
            RoadtripSettings roadtripSettings = roadtripSettingsRepository.findByRoadtrip_RoadtripId(roadtripId)
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip settings not found"));

            // Update fields if they are not null
            if (updatedRoadtripSettings.getBasemapType() != null) {
                roadtripSettings.setBasemapType(updatedRoadtripSettings.getBasemapType());
            }
            if (updatedRoadtripSettings.getDecisionProcess() != null) {
                roadtripSettings.setDecisionProcess(updatedRoadtripSettings.getDecisionProcess());
            }
            if (updatedRoadtripSettings.getBoundingBox() != null) {
                roadtripSettings.setBoundingBox(updatedRoadtripSettings.getBoundingBox());
            }

            // Validate dates
            if (updatedRoadtripSettings.getStartDate() != null && updatedRoadtripSettings.getEndDate() != null) {
                LocalDate startDate = updatedRoadtripSettings.getStartDate();
                LocalDate endDate = updatedRoadtripSettings.getEndDate();

                if (endDate.isBefore(startDate)) {
                    throw new IllegalArgumentException("End date cannot be before start date");
                }

                roadtripSettings.setStartDate(startDate);
                roadtripSettings.setEndDate(endDate);
            } else {
                // Update individual dates if provided
                if (updatedRoadtripSettings.getStartDate() != null) {
                    LocalDate startDate = updatedRoadtripSettings.getStartDate();
                    LocalDate currentEndDate = roadtripSettings.getEndDate();

                    if (currentEndDate != null && startDate.isAfter(currentEndDate)) {
                        throw new IllegalArgumentException("Start date cannot be after end date");
                    }

                    roadtripSettings.setStartDate(startDate);
                }

                if (updatedRoadtripSettings.getEndDate() != null) {
                    LocalDate endDate = updatedRoadtripSettings.getEndDate();
                    LocalDate currentStartDate = roadtripSettings.getStartDate();

                    if (currentStartDate != null && endDate.isBefore(currentStartDate)) {
                        throw new IllegalArgumentException("End date cannot be before start date");
                    }

                    roadtripSettings.setEndDate(endDate);
                }
            }

            this.roadtripSettingsRepository.save(roadtripSettings);
            roadtripSettingsRepository.flush();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (Exception e) {
            if (e instanceof ResponseStatusException) {
                throw e;
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "An error occurred while updating the settings: " + e.getMessage());
        }
    }

    public String uploadRoadtripImage(MultipartFile file, String bucketName, Long roadtripId, User user) {
        // Check if roadtrip exists
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));

        // Check if user is owner
        boolean isOwner = Objects.equals(roadtrip.getOwner().getUserId(), user.getUserId());

        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the roadtrip owner can access the settings");
        }

        RoadtripSettings roadtripSettings = roadtripSettingsRepository.findByRoadtrip_RoadtripId(roadtripId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip settings not found"));

        try {
            String imageName = new Date().getTime() + "-" + roadtripId.toString();
            String fileUrl = storageService.uploadFile(file, bucketName, imageName);

            roadtripSettings.setImageName(imageName);
            roadtripSettings.setImageLocation(fileUrl);
            return imageName;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload file", e);
        }
    }

    public String getRoadtripImageName(Long roadtripId, User user) {

        // Check if roadtrip exists and get settings
        RoadtripSettings roadtripSettings = roadtripSettingsRepository.findByRoadtrip_RoadtripId(roadtripId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip settings not found"));

        // Check if user is roadtrip member

        // Check if there is an image and fetch it's name
        String fileName = roadtripSettings.getImageName();
        if (fileName == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No image found for this roadtrip");
        }

        return fileName;
    }

    public void deleteRoadtripImage(Long roadtripId, String bucketName, User user) {
        // Check if roadtrip exists
        Roadtrip roadtrip = roadtripRepository.findById(roadtripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip not found"));

        // Check if user is owner
        boolean isOwner = Objects.equals(roadtrip.getOwner().getUserId(), user.getUserId());

        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the roadtrip owner can access the settings");
        }

        RoadtripSettings roadtripSettings = roadtripSettingsRepository.findByRoadtrip_RoadtripId(roadtripId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roadtrip settings not found"));

        String fileName = roadtripSettings.getImageName();
        if (fileName != null) {
            storageService.deleteFile(bucketName, fileName);
            roadtripSettings.setImageName(null);
            roadtripSettings.setImageLocation(null);
            roadtripSettingsRepository.save(roadtripSettings);
        }
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
