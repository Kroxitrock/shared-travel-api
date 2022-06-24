package eu.sharedtravel.app.components.profilesettings.repository.predicate;

import eu.sharedtravel.app.components.profilesettings.model.QProfileSettings;
import org.springframework.stereotype.Component;

@Component
public class ProfileSettingsPredicates {

    private final QProfileSettings qProfileSettings;

    public ProfileSettingsPredicates() {
        this.qProfileSettings = QProfileSettings.profileSettings;
    }
}
