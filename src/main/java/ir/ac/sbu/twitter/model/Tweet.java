package ir.ac.sbu.twitter.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Data
@Entity
@Table(name="TWEETS")
@EqualsAndHashCode(of = "id")
public class Tweet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BODY")
    private String body;
    @Column(name = "DATE")
    private Date date;
    private long userId;
    @ElementCollection
    private List<String> hashtags;
}
