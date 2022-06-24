package eu.sharedtravel.app.components.profile.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.sharedtravel.app.components.profilesettings.model.ProfileSettings;
import eu.sharedtravel.app.components.user.model.User;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @SequenceGenerator(name = "profilesIdSeq", sequenceName = "profiles_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "profilesIdSeq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank
    @Size(max = 25)
    private String firstName;

    @NotBlank
    @Size(max = 25)
    private String lastName;

    @NotNull
    @OneToOne(cascade = CascadeType.MERGE)
    private User user;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private ProfileSettings profileSettings;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Profile profile = (Profile) o;
        return id != null && Objects.equals(id, profile.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Transient
    @JsonIgnore
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
