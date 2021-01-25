package ir.ac.sbu.twitter.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TweetDto {
    private long id;
    private String body;
    private UserDto author;
    private int likes;
    private int retweet;
    private Date date;
    private boolean isLiked;
    private boolean isRetweeted;
    private String picture;
}
