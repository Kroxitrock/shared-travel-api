package eu.sharedtravel.app.components.profilesettings.service.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProfileSettingsDto {

    @NotNull
    private Long id;

    @NotNull
    private Boolean emailVisible;
}
