package eu.sharedtravel.app.components.location.repository;

import eu.sharedtravel.app.common.repository.ReadOnlyRepository;
import eu.sharedtravel.app.components.location.model.Location;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends ReadOnlyRepository<Location, String> {

}
