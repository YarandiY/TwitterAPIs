package ir.ac.sbu.twitter.dto;

import lombok.Data;

@Data
public class TweetDto {
    private String body;
    private UserDto author;
    private int likes;
    private int retweet;
}
