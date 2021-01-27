package ir.ac.sbu.twitter.model;

import ir.ac.sbu.twitter.dto.TweetDto;
import ir.ac.sbu.twitter.dto.UserDto;
import ir.ac.sbu.twitter.exception.InvalidInput;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@Entity
@Table(name="TWEETS7")
@EqualsAndHashCode(of = "ID")
public class Tweet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BODY")
    private String body;
    @Column(name = "PIC")
    private String picture;
    @Column(name = "DATE")
    private Date date;
    @Column(name = "AUTHOR")
    private Long authorId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "TWEETS_HASHTAGS7",
            joinColumns = @JoinColumn(name = "TWEETS_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "HASHTAGS_ID", referencedColumnName = "ID"))
    private List<Hashtag> hashtags;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "TWEETS_USERS_LIKE7",
            joinColumns = @JoinColumn(name = "TWEETS_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "USERS_ID", referencedColumnName = "ID"))
    private List<User> liked;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "TWEETS_USERS_RET7",
            joinColumns = @JoinColumn(name = "TWEETS_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "USERS_ID", referencedColumnName = "ID"))
    private List<User> retweets;

    public TweetDto getDto(UserDto author, boolean isLiked, boolean isRetweeted){
        TweetDto dto = new TweetDto();
        dto.setId(id);
        dto.setLiked(isLiked);
        dto.setRetweeted(isRetweeted);
        dto.setDate(date);
        dto.setAuthor(author);
        dto.setBody(body);
        dto.setPicture(picture);
        if(liked == null)
            liked = new ArrayList<>();
        dto.setLikes(liked.size());
        if(retweets == null)
            retweets = new ArrayList<>();
        dto.setRetweet(retweets.size());
        return dto;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Tweet)
            return ((Tweet)o).getId().equals(this.id);
        return false;
    }
}
