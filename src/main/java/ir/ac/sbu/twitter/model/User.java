package ir.ac.sbu.twitter.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@Entity
@Table(name="USERS3")
public class User  implements Serializable, UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME", unique = true)
    private String username;
    @Column(name = "EMAIL", unique = true)
    private String email;
    @Column(name = "PASSWORD")
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "USERS_TWEETS3",
            joinColumns = @JoinColumn(name = "USERS_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "TWEETS_ID", referencedColumnName = "ID"))
    @OrderBy
    private List<Tweet> tweets;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "USERS_USERS3", joinColumns = @JoinColumn(name = "USER_ID1", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID2", referencedColumnName = "ID"))
    @OrderBy
    private List<User> followings;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
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