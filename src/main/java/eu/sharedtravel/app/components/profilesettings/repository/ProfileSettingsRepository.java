package eu.sharedtravel.app.components.profilesettings.repository;

import eu.sharedtravel.app.components.profilesettings.model.ProfileSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileSettingsRepository extends JpaRepository<ProfileSettings, Long>,
    QuerydslPredicateExecutor<ProfileSettings> {

}
