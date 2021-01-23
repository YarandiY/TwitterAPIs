package ir.ac.sbu.twitter.model;

import ir.ac.sbu.twitter.dto.TweetDto;
import ir.ac.sbu.twitter.dto.UserDto;
import ir.ac.sbu.twitter.exception.InvalidInput;
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
    @Column(name = "AUTHOR")
    private Long authorId;
    @ElementCollection
    private List<String> hashtags;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "TWEETS_USERS", joinColumns = @JoinColumn(name = "TWEET_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"))
    @OrderBy
    private List<User> liked;

    public TweetDto getDto(UserDto author){
        TweetDto dto = new TweetDto();
        dto.setAuthor(author);
        dto.setBody(body);
        dto.setLikes(liked.size());
        return dto;
    }
}
