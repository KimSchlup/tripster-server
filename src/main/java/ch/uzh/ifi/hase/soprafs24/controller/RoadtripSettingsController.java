package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs24.entity.RoadtripSettings;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripSettingsGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripSettingsPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.RoadtripSettingsService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

@RestController
@RequestMapping("/roadtrips")
public class RoadtripSettingsController {

    private final UserService userService;
    private final RoadtripSettingsService roadtripSettingsService;

    RoadtripSettingsController(UserService userService,
            RoadtripSettingsService roadtripSettingsService) {
        this.roadtripSettingsService = roadtripSettingsService;
        this.userService = userService;
    }

    /**
     * GET /roadtrips/{roadtripId}/settings
     * A user which is owner can access the settings of a roadtrip
     * 
     * @return RoadtripGetDTO
     */
    @GetMapping("/{roadtripId}/settings")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RoadtripSettingsGetDTO getRoadtripById(@PathVariable Long roadtripId,
            @RequestHeader("Authorization") String token) {

        // Get user from token
        User user = userService.getUserByToken(token);

        // Fetch roadtrips settings if user is owner of roadtrip
        RoadtripSettings roadtripSettings = roadtripSettingsService.getRoadtripSettingsById(roadtripId, user);

        // convert internal representation of rodatrip back to API
        RoadtripSettingsGetDTO roadtripSettingsGetDTO = DTOMapper.INSTANCE
                .convertEntityToRoadtripSettingsGetDTO(roadtripSettings);

        return roadtripSettingsGetDTO;
    }

    /**
     * PUT /roadtrips/{roadtripId}/settings
     * A user which is owner can update the settings of a roadtrip
     * 
     * @return
     */
    @PutMapping("/{roadtripId}/settings")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateRoadtripSettingsById(@PathVariable Long roadtripId,
            @RequestBody RoadtripSettingsPutDTO roadtripSettingsPutDTO,
            @RequestHeader("Authorization") String token) {

        // Get user from token
        User user = userService.getUserByToken(token);

        RoadtripSettings roadtripSettingsInput = DTOMapper.INSTANCE
                .convertRoadtripSettingsPutDTOtoEntity(roadtripSettingsPutDTO);

        // Fetch roadtrips user is owner of or member of
        roadtripSettingsService.updateRoadtripSettingsById(roadtripId, roadtripSettingsInput, user);

    }
}
