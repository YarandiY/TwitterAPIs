package ir.ac.sbu.twitter.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="FOLLOWS")
@EqualsAndHashCode(of = "ID")
public class FollowLog implements Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "FOLLOWING")
    private long followingId;
    @Column(name = "USERID")
    private long userId;
    @Column(name = "DATE")
    private Date date;

    @Override
    public long getTweetId() {
        return -1;
    }
}
