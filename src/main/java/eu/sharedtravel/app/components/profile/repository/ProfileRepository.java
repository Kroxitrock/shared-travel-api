package eu.sharedtravel.app.components.profile.repository;

import eu.sharedtravel.app.components.profile.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long>, QuerydslPredicateExecutor<Profile> {

}
