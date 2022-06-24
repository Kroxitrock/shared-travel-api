package eu.sharedtravel.app.components.profilesettings.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "profile_settings")
public class ProfileSettings {

    @Id
    @SequenceGenerator(name = "profileSettingsIdSeq", sequenceName = "profile_settings_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "profileSettingsIdSeq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    private Boolean emailVisible = true;
}
