package eu.sharedtravel.app.components.user.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * Authorities used for user authentication
 */
public enum UserAuthority implements GrantedAuthority {
    /**
     * Default authority given to all users after they register
     */
    USER,
    /**
     * Authority given to users that have enabled the driver options - creating a new travel, importing vehicles, etc.
     */
    DRIVER;

    @Override
    public String getAuthority() {
        return name();
    }
}
