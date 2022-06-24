package eu.sharedtravel.app.config.properties;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CorsProps {

    @NotNull
    private String[] allowed;
}
