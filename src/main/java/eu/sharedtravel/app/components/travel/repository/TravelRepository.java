package eu.sharedtravel.app.components.travel.repository;

import eu.sharedtravel.app.components.travel.model.Travel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelRepository extends JpaRepository<Travel, Long>, QuerydslPredicateExecutor<Travel> {

}
