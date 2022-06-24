package eu.sharedtravel.app.components.travel;

import eu.sharedtravel.app.components.location.LocationTestConstants;
import eu.sharedtravel.app.components.location.model.Location;
import eu.sharedtravel.app.components.profile.ProfileTestMocks;
import eu.sharedtravel.app.components.travel.model.Travel;
import eu.sharedtravel.app.components.travel.service.dto.TravelWithIsAppliedOutputDto;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

public class TravelTestMocks {

    public static Page<Travel> mockTravelPage() {
        return new PageImpl<>(Collections.singletonList(mockTravel()));
    }

    public static Travel mockTravel() {
        return Travel.builder()
            .id(TravelTestConstants.ID)
            .from(new Location(LocationTestConstants.SOF_CODE, LocationTestConstants.SOF_NAME))
            .to(new Location(LocationTestConstants.DB_CODE, LocationTestConstants.DB_NAME))
            .departureDate(LocalDateTime.now())
            .status(TravelTestConstants.STATUS)
            .driver(ProfileTestMocks.mockDriverProfile())
            .passengers(new HashSet<>(Collections.singletonList(ProfileTestMocks.mockUserProfile())))
            .build();
    }

    public static TravelWithIsAppliedOutputDto mockTravelWithIsAppliedOutputDto() {
        return TravelWithIsAppliedOutputDto.builder()
            .id(TravelTestConstants.ID)
            .from(LocationTestConstants.SOF_CODE)
            .to(LocationTestConstants.DB_CODE)
            .departureDate(LocalDateTime.now())
            .status(TravelTestConstants.STATUS)
            .driver(ProfileTestMocks.mockDriverProfileOutputDto())
            .passengers(new HashSet<>(Collections.singletonList(ProfileTestMocks.mockProfileOutputDto())))
            .build();
    }
}
