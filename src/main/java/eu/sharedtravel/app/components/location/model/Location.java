package eu.sharedtravel.app.components.location.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Immutable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Immutable
@Table(name = "n_locations")
public class Location {

    @Id
    @NotNull
    @Size(max = 5)
    private String code;

    @NotNull
    @Size(max = 50)
    private String name;

}
