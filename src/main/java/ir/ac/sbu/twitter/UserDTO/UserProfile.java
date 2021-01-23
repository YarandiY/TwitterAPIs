package ir.ac.sbu.twitter.UserDTO;

import ir.ac.sbu.twitter.model.Tweet;
import ir.ac.sbu.twitter.model.User;
import lombok.Data;

import java.util.List;

@Data
public class UserProfile {
    private String username;
    private String email;
    private List<Tweet> tweets; //TODO
    private List<User> followers;
}
