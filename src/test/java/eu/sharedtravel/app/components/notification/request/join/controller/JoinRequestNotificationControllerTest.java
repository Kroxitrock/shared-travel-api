package eu.sharedtravel.app.components.notification.request.join.controller;

import static eu.sharedtravel.app.common.Constants.BEARER_PREFIX;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.sharedtravel.app.components.notification.NotificationTestMocks;
import eu.sharedtravel.app.components.notification.request.join.model.JoinRequestNotification;
import eu.sharedtravel.app.components.notification.request.join.service.JoinRequestNotificationService;
import eu.sharedtravel.app.components.profile.ProfileTestMocks;
import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.travel.TravelTestConstants;
import eu.sharedtravel.app.components.travel.TravelTestMocks;
import eu.sharedtravel.app.components.travel.service.TravelService;
import eu.sharedtravel.app.components.user.UserTestMocks;
import eu.sharedtravel.app.components.user.service.dto.UserDto;
import eu.sharedtravel.app.config.security.JWTGenerator;
import java.util.Collections;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class JoinRequestNotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JoinRequestNotificationService joinRequestNotificationService;
    @MockBean
    private TravelService travelService;

    @Autowired
    private JWTGenerator jwtGenerator;

    private JoinRequestNotification joinRequest;
    private Profile driver;
    private UserDto driverDto;
    private Profile passenger;
    private UserDto passengerDto;

    @BeforeEach
    void setUp() {
        joinRequest = NotificationTestMocks.mockJoinNotification();
        driver = ProfileTestMocks.mockDriverProfile();
        driverDto = UserTestMocks.mockDriverUserDto();
        passenger = ProfileTestMocks.mockUserProfile();
        passengerDto = UserTestMocks.mockUserDto();
    }

    @Test
    void givenIdShouldGetJoinRequestNotification() throws Exception {
        var notification = NotificationTestMocks.mockJoinNotification();

        Mockito.when(joinRequestNotificationService.findJoinRequestNotification(notification.getId()))
            .thenReturn(notification);

        mockMvc.perform(get("/notifications/requests/join/" + notification.getId())
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + jwtGenerator.generateJWT(driver.getUser())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", Matchers.is(notification.getId().intValue())));

        Mockito.verify(joinRequestNotificationService).validateUserIsDriverOfRequest(driverDto, joinRequest);
    }

    @Test
    void givenDriverAndJoinRequestIdShouldAcceptNotification() throws Exception {
        Mockito.when(joinRequestNotificationService.getActiveJoinRequestNotification(joinRequest.getId()))
            .thenReturn(joinRequest);

        mockMvc.perform(post("/notifications/requests/join/" + joinRequest.getId() + "/accept")
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + jwtGenerator.generateJWT(driver.getUser())))
            .andExpect(status().isOk());

        Mockito.verify(joinRequestNotificationService).validateUserIsDriverOfRequest(driverDto, joinRequest);
        Mockito.verify(joinRequestNotificationService).accept(joinRequest);
    }

    @Test
    void givenDriverAndJoinRequestIdShouldRejectNotification() throws Exception {
        Mockito.when(joinRequestNotificationService.getActiveJoinRequestNotification(joinRequest.getId()))
            .thenReturn(joinRequest);

        mockMvc.perform(post("/notifications/requests/join/" + joinRequest.getId() + "/reject")
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + jwtGenerator.generateJWT(driver.getUser())))
            .andExpect(status().isOk());

        Mockito.verify(joinRequestNotificationService).validateUserIsDriverOfRequest(driverDto, joinRequest);
        Mockito.verify(joinRequestNotificationService).reject(joinRequest);
    }

    @Test
    void givenDriverAndJoinRequestIdShouldCancelNotification() throws Exception {
        var joinRequestNotifications = Collections.singletonList(joinRequest);

        Mockito.when(joinRequestNotificationService.getPendingJoinRequestNotificationsForTravelAndPassenger(
            TravelTestConstants.ID, passengerDto.getId())).thenReturn(joinRequestNotifications);

        mockMvc.perform(post("/notifications/requests/join/pending/travel/" + TravelTestConstants.ID + "/cancel")
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + jwtGenerator.generateJWT(passenger.getUser())))
            .andExpect(status().isOk());

        Mockito.verify(joinRequestNotificationService).cancel(joinRequestNotifications);
    }

    @Test
    void givenDriverAndJoinRequestIdWhenJoinRequestsEmptyShouldThrowException() throws Exception {
        Mockito.when(joinRequestNotificationService.getPendingJoinRequestNotificationsForTravelAndPassenger(
            TravelTestConstants.ID, passengerDto.getId())).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/notifications/requests/join/pending/travel/" + TravelTestConstants.ID + "/cancel")
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + jwtGenerator.generateJWT(passenger.getUser())))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message", Matchers.equalTo(
                String.format("No join request found for travelId %d and applicant with id %d!", TravelTestConstants.ID,
                    passengerDto.getId()))));
    }

    @Test
    void givenDriverAndTravelIdShouldReturnPendingJoinRequests() throws Exception {
        var travel = TravelTestMocks.mockTravel();
        var driverProfile = ProfileTestMocks.mockDriverProfileDto();

        Mockito.when(travelService.getTravel(travel.getId(), driverProfile.getId())).thenReturn(travel);
        Mockito.when(joinRequestNotificationService.getPendingJoinRequestNotifications(travel))
            .thenReturn(Collections.singletonList(joinRequest));

        mockMvc.perform(get("/notifications/requests/join/pending/travel/" + travel.getId())
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + jwtGenerator.generateJWT(driver.getUser())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", Matchers.equalTo(joinRequest.getId().intValue())));
    }
}
