package ir.ac.sbu.twitter.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name="USERS")
@EqualsAndHashCode(of = "id")
public class User  implements Serializable {
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
    @JoinTable(name = "USERS_TWEETS", joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "TWEET_ID", referencedColumnName = "ID"))
    @OrderBy
    private List<Tweet> tweets;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "USERS_USERS", joinColumns = @JoinColumn(name = "USER_ID1", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID2", referencedColumnName = "ID"))
    @OrderBy
    private List<User> follower;
}