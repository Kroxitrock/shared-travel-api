package eu.sharedtravel.app.components.notification.request.join.model;

import eu.sharedtravel.app.components.notification.model.Notification;
import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.travel.model.Travel;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@PrimaryKeyJoinColumn(name = "notification_id")
@Table(name = "join_request_notifications")
public class JoinRequestNotification extends Notification {

    @NotNull
    @ManyToOne
    private Profile passenger;

    @NotNull
    @ManyToOne
    private Travel travel;

    @Enumerated(EnumType.STRING)
    private JoinRequestStatus status;

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
        Notification that = (Notification) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
