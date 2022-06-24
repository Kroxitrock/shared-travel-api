package eu.sharedtravel.app.components.notification.controller;

import static eu.sharedtravel.app.common.Constants.BEARER_PREFIX;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.sharedtravel.app.components.notification.NotificationTestConstants;
import eu.sharedtravel.app.components.notification.NotificationTestMocks;
import eu.sharedtravel.app.components.notification.service.NotificationService;
import eu.sharedtravel.app.components.user.UserTestMocks;
import eu.sharedtravel.app.config.security.JWTGenerator;
import org.hamcrest.Matchers;
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
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private JWTGenerator jwtGenerator;

    @Test
    void givenProfileShouldGetNotificationsAndMap() throws Exception {
        var mockNotificationPage = NotificationTestMocks.mockNotificationPage();

        Mockito.when(notificationService.findNotifications(Mockito.any(), Mockito.any()))
            .thenReturn(mockNotificationPage);

        mockMvc.perform(get("/notifications")
                .header(HttpHeaders.AUTHORIZATION,
                    BEARER_PREFIX + jwtGenerator.generateJWT(UserTestMocks.mockDriverUser())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", Matchers.hasSize(1)))
            .andExpect(jsonPath("$.content.[0].id", Matchers.is(1)))
            .andExpect(jsonPath("$.content.[0].type", Matchers.is(NotificationTestConstants.TYPE.toString())));
    }

    @Test
    void givenProfileShouldGetNotificationCount() throws Exception {
        Mockito.when(notificationService.notificationCount(Mockito.any()))
            .thenReturn(1L);

        mockMvc.perform(get("/notifications/count")
                .header(HttpHeaders.AUTHORIZATION,
                    BEARER_PREFIX + jwtGenerator.generateJWT(UserTestMocks.mockDriverUser())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", Matchers.is(1)));
    }
}
