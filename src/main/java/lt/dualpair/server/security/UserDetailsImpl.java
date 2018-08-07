package lt.dualpair.server.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserDetailsImpl implements UserDetails {

    private Long userId;

    public UserDetailsImpl(Long userId) {
        this.userId = userId;
    }

    @Override
    public Long getId() {
        return userId;
    }

    @Override
    public String getUserId() {
        return userId.toString();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return userId.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
