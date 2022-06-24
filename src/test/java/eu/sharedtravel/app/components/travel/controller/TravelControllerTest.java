package eu.sharedtravel.app.components.travel.controller;

import static eu.sharedtravel.app.common.Constants.BEARER_PREFIX;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.sharedtravel.app.components.location.LocationTestConstants;
import eu.sharedtravel.app.components.notification.NotificationTestMocks;
import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestNotification;
import eu.sharedtravel.app.components.notification.request.join.service.JoinRequestNotificationService;
import eu.sharedtravel.app.components.profile.ProfileTestMocks;
import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.profile.service.dto.ProfileDto;
import eu.sharedtravel.app.components.travel.TravelTestConstants;
import eu.sharedtravel.app.components.travel.TravelTestMocks;
import eu.sharedtravel.app.components.travel.model.Travel;
import eu.sharedtravel.app.components.travel.model.TravelStatus;
import eu.sharedtravel.app.components.travel.service.TravelService;
import eu.sharedtravel.app.components.travel.service.dto.TravelFilterDto;
import eu.sharedtravel.app.components.travel.service.dto.TravelInputDto;
import eu.sharedtravel.app.components.travel.service.dto.TravelUpdateDto;
import eu.sharedtravel.app.components.user.UserTestMocks;
import eu.sharedtravel.app.config.security.JWTGenerator;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SuppressWarnings("squid:S2699")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class TravelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TravelService travelService;

    @MockBean
    private JoinRequestNotificationService joinRequestNotificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JWTGenerator jwtGenerator;

    @Test
    void givenTravelPageWhenHavingJWTShouldMap() throws Exception {
        Page<Travel> travels = TravelTestMocks.mockTravelPage();
        Travel travel = TravelTestMocks.mockTravel();
        TravelFilterDto dto = new TravelFilterDto(
            travel.getFrom().getCode(),
            travel.getTo().getCode(),
            travel.getDepartureDate());

        Mockito.when(travelService.findFilteredTravels(Mockito.any(), Mockito.any())).thenReturn(travels);

        mockMvc.perform(get("/travels")
                .param("from", dto.getFrom())
                .param("to", dto.getTo())
                .param("departureDate", dto.getDepartureDate().toString())
                .header(HttpHeaders.AUTHORIZATION,
                    BEARER_PREFIX + jwtGenerator.generateJWT(UserTestMocks.mockDriverUser())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", Matchers.hasSize(1)))
            .andExpect(jsonPath("$.content.[0].from", Matchers.is(LocationTestConstants.SOF_CODE)))
            .andExpect(jsonPath("$.content.[0].to", Matchers.is(LocationTestConstants.DB_CODE)));
    }

    @Test
    void givenTravelPageShouldMap() throws Exception {
        Page<Travel> travels = TravelTestMocks.mockTravelPage();
        Travel travel = TravelTestMocks.mockTravel();
        TravelFilterDto dto = new TravelFilterDto(
            travel.getFrom().getCode(),
            travel.getTo().getCode(),
            travel.getDepartureDate());

        Mockito.when(travelService.findFilteredTravels(Mockito.any(), Mockito.any())).thenReturn(travels);

        mockMvc.perform(get("/travels")
                .param("from", dto.getFrom())
                .param("to", dto.getTo())
                .param("departureDate", dto.getDepartureDate().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", Matchers.hasSize(1)))
            .andExpect(jsonPath("$.content.[0].from", Matchers.is(LocationTestConstants.SOF_CODE)))
            .andExpect(jsonPath("$.content.[0].to", Matchers.is(LocationTestConstants.DB_CODE)));
    }

    @Test
    void givenMyTravelPageShouldMap() throws Exception {
        Page<Travel> travels = TravelTestMocks.mockTravelPage();

        Mockito
            .when(travelService.findMyTravels(Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.any(), Mockito.any()))
            .thenReturn(travels);

        mockMvc.perform(get("/travels/my")
                .param("driverOnly", String.valueOf(false))
                .param("inFuture", String.valueOf(false))
                .header(HttpHeaders.AUTHORIZATION,
                    BEARER_PREFIX + jwtGenerator.generateJWT(UserTestMocks.mockDriverUser())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", Matchers.hasSize(1)))
            .andExpect(jsonPath("$.content.[0].from", Matchers.is(LocationTestConstants.SOF_CODE)))
            .andExpect(jsonPath("$.content.[0].to", Matchers.is(LocationTestConstants.DB_CODE)));
    }

    @Test
    void givenTravelShouldMap() throws Exception {
        Travel travel = TravelTestMocks.mockTravel();
        TravelInputDto dto = new TravelInputDto(
            travel.getFrom().getCode(),
            travel.getTo().getCode(),
            travel.getDepartureDate());

        Mockito.when(travelService.saveTravel(Mockito.any(), Mockito.any())).thenReturn(travel);

        mockMvc.perform(post("/travels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header(HttpHeaders.AUTHORIZATION,
                    BEARER_PREFIX + jwtGenerator.generateJWT(UserTestMocks.mockDriverUser())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.from", Matchers.is(LocationTestConstants.SOF_CODE)))
            .andExpect(jsonPath("$.to", Matchers.is(LocationTestConstants.DB_CODE)));
    }

    @Test
    void givenTravelShouldUpdateAndMap() throws Exception {
        Travel travel = TravelTestMocks.mockTravel();
        TravelUpdateDto dto = new TravelUpdateDto(
            travel.getFrom().getCode(),
            travel.getTo().getCode(),
            travel.getDepartureDate(),
            travel.getStatus());

        Mockito.when(travelService.updateTravel(Mockito.anyLong(), Mockito.any(), Mockito.anyLong()))
            .thenReturn(travel);

        mockMvc.perform(put("/travels/" + travel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .header(HttpHeaders.AUTHORIZATION,
                    BEARER_PREFIX + jwtGenerator.generateJWT(UserTestMocks.mockDriverUser())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", Matchers.is(TravelTestConstants.ID.intValue())))
            .andExpect(jsonPath("$.from", Matchers.is(LocationTestConstants.SOF_CODE)))
            .andExpect(jsonPath("$.to", Matchers.is(LocationTestConstants.DB_CODE)));
    }

    @Test
    void givenTravelIdShouldChangeTravelStatusAndReturnIt() throws Exception {
        Travel travel = TravelTestMocks.mockTravel();
        JoinRequestNotification joinRequest = NotificationTestMocks.mockJoinNotification();
        List<JoinRequestNotification> joinRequests = Collections.singletonList(joinRequest);
        ProfileDto driver = ProfileTestMocks.mockDriverProfileDto();
        Travel canceledTravel = TravelTestMocks.mockTravel();
        List<Profile> applicants = Collections.singletonList(joinRequest.getPassenger());
        canceledTravel.setStatus(TravelStatus.CANCELED);

        Mockito.when(travelService.getActiveTravelById(travel.getId())).thenReturn(travel);
        Mockito.when(joinRequestNotificationService.getPendingJoinRequestNotifications(travel))
            .thenReturn(joinRequests);

        Mockito.when(travelService.cancelTravel(travel, driver, applicants))
            .thenReturn(canceledTravel);

        mockMvc.perform(patch("/travels/" + travel.getId() + "/cancel")
                .param("status", TravelStatus.CANCELED.toString())
                .header(HttpHeaders.AUTHORIZATION,
                    BEARER_PREFIX + jwtGenerator.generateJWT(UserTestMocks.mockDriverUser())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", Matchers.is(TravelTestConstants.ID.intValue())))
            .andExpect(jsonPath("$.status", Matchers.is(TravelStatus.CANCELED.toString())));
    }

    @Test
    void givenTravelIdAndPassengerShouldRemovePassengerFromTravel() throws Exception {
        var travel = TravelTestMocks.mockTravel();
        var passenger = ProfileTestMocks.mockUserProfile();

        Mockito.when(travelService.getActiveTravelById(travel.getId())).thenReturn(travel);

        mockMvc.perform(post("/travels/" + travel.getId() + "/leave")
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + jwtGenerator.generateJWT(passenger.getUser())))
            .andExpect(status().isOk());

        Mockito.verify(travelService).removePassengerFromTravel(passenger, travel, true);
    }

    @Test
    void givenTravelIdPassengerIdAndDriverShouldKickPersonFromTravel() throws Exception {
        var travel = TravelTestMocks.mockTravel();
        var passenger = ProfileTestMocks.mockUserProfile();
        var driver = ProfileTestMocks.mockDriverProfile();

        Mockito.when(travelService.getActiveTravelById(travel.getId())).thenReturn(travel);

        mockMvc.perform(post("/travels/" + travel.getId() + "/passengers/" + passenger.getId() + "/kick")
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + jwtGenerator.generateJWT(driver.getUser())))
            .andExpect(status().isOk());

        Mockito.verify(travelService).removePassengerFromTravel(passenger, travel, false);
    }

    @Test
    void givenUserNotDriverWhenKickPersonFromTravelShouldThrowException() throws Exception {
        var travel = TravelTestMocks.mockTravel();
        var passenger = ProfileTestMocks.mockUserProfile();
        var driver = ProfileTestMocks.mockDriverProfile();
        travel.setDriver(passenger);

        Mockito.when(travelService.getActiveTravelById(travel.getId())).thenReturn(travel);

        mockMvc.perform(post("/travels/" + travel.getId() + "/passengers/" + passenger.getId() + "/kick")
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + jwtGenerator.generateJWT(driver.getUser())))
            .andExpect(status().isForbidden());
    }

    @Test
    void givenTravelIdWhenRequestJoinTravelShouldNotThrowExceptions() throws Exception {
        var travelId = TravelTestConstants.ID;
        var travel = TravelTestMocks.mockTravel();
        var passenger = ProfileTestMocks.mockUserProfile();

        Mockito.when(travelService.getActiveTravelById(travelId)).thenReturn(travel);

        mockMvc.perform(post("/travels/" + travelId + "/join")
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + jwtGenerator.generateJWT(passenger.getUser())))
            .andExpect(status().isOk());

        Mockito.verify(joinRequestNotificationService).createJoinTravelNotification(travel, passenger);
    }
}
