package ch.uzh.ifi.hase.soprafs24.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.ifi.hase.soprafs24.entity.RoadtripSettings;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripSettingsGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.RoadtripSettingsPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.RoadtripSettingsService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.utils.GenerateV4GetObjectSignedUrl;

@RestController
@RequestMapping("/roadtrips")
public class RoadtripSettingsController {

    private final UserService userService;
    private final RoadtripSettingsService roadtripSettingsService;
    private final GenerateV4GetObjectSignedUrl signedUrlGenerator;

    @Value("mapmates-object-store")
    private String bucketName;

    RoadtripSettingsController(UserService userService,
            RoadtripSettingsService roadtripSettingsService,
            GenerateV4GetObjectSignedUrl signedUrlGenerator) {
        this.roadtripSettingsService = roadtripSettingsService;
        this.userService = userService;
        this.signedUrlGenerator = signedUrlGenerator;

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

    @PostMapping("/{roadtripId}/settings/images")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String uploadRoadtripImage(@PathVariable Long roadtripId, @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token) {

        // Get user from token
        User user = userService.getUserByToken(token);

        String imageName = roadtripSettingsService.uploadRoadtripImage(file, bucketName, roadtripId, user);
        return imageName;

    }

    @GetMapping("/{roadtripId}/settings/images")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String downloadRoadtripImage(@PathVariable Long roadtripId,
            @RequestHeader("Authorization") String token) {

        // Get user from token
        User user = userService.getUserByToken(token);

        // Get name of the roadtirp image and create signed url
        String roadtripImageName = roadtripSettingsService.getRoadtripImageName(roadtripId, user);
        String signedUrl = signedUrlGenerator.generateV4GetObjectSignedUrl(
                "sopra-fs25-group-08-server", bucketName, "uploads/" + roadtripImageName);

        return signedUrl;
    }

}
