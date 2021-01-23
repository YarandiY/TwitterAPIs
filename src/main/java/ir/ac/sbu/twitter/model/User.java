package ir.ac.sbu.twitter.model;

import lombok.Data;

import java.util.List;

@Data
public class User {
    private String username;
    private String email;
    private String password;
    private List<Tweet> tweets;
    private List<Tweet> likedTweets;
    private List<User> follower;
    private List<User> following;
}
