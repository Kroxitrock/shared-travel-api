package eu.sharedtravel.app.components.travel.model;

import eu.sharedtravel.app.components.location.model.Location;
import eu.sharedtravel.app.components.profile.model.Profile;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "travels")
public class Travel {

    @Id
    @SequenceGenerator(name = "travelsIdSeq", sequenceName = "travels_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "travelsIdSeq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @ManyToOne
    private Location from;

    @NotNull
    @ManyToOne
    private Location to;

    @NotNull
    private LocalDateTime departureDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TravelStatus status = TravelStatus.PENDING;

    @NotNull
    @ManyToOne
    private Profile driver;

    @ManyToMany
    @JoinTable(name = "profiles_travels",
        joinColumns = @JoinColumn(name = "travel_id"),
        inverseJoinColumns = @JoinColumn(name = "profile_id"))
    @ToString.Exclude
    private Set<Profile> passengers = new HashSet<>();

    public void addPassenger(Profile passenger) {
        passengers.add(passenger);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Travel travel = (Travel) o;
        return id != null && Objects.equals(id, travel.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
