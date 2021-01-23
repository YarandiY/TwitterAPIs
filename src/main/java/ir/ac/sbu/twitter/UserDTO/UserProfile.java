package ir.ac.sbu.twitter.UserDTO;

import lombok.Data;

import java.util.List;

@Data
public class UserProfile {
    private String username;
    private String email;
    private List<Long> tweets;
    private List<Long> followers;
    private List<Long> following;
    private List<Long> likedTweets;

}
