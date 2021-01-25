package ir.ac.sbu.twitter.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="RETWEETS")
@EqualsAndHashCode(of = "ID")
public class RetweetLog implements Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "TWEETID")
    private long tweetId;
    @Column(name = "USERID")
    private long userId;
    @Column(name = "DATE")
    private Date date;

    @Override
    public long getFollowingId() {
        return -1;
    }
}
