package ir.ac.sbu.twitter.service;

import ir.ac.sbu.twitter.UserDTO.UserCreate;
import ir.ac.sbu.twitter.UserDTO.UserProfile;
import ir.ac.sbu.twitter.exception.DuplicateInputError;
import ir.ac.sbu.twitter.exception.InvalidInput;
import ir.ac.sbu.twitter.model.Tweet;
import ir.ac.sbu.twitter.model.User;
import ir.ac.sbu.twitter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User create(UserCreate userCreate) throws DuplicateInputError {
        if(userRepository.findByEmail(userCreate.getEmail()).isPresent())
            throw new DuplicateInputError();
        if(userRepository.findByUsername(userCreate.getUsername()).isPresent())
            throw new DuplicateInputError();
        User user = new User();
        user.setEmail(userCreate.getEmail());
        user.setPassword(userCreate.getPassword());
        user.setUsername(userCreate.getUsername());
        user.setFollower(new ArrayList<>());
        user.setTweets(new ArrayList<>());
        return userRepository.save(user);
    }

    public UserProfile getProfile(long id) throws InvalidInput {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty())
            throw new InvalidInput();
        User user = optionalUser.get();
        UserProfile userProfile = new UserProfile();
        userProfile.setEmail(user.getEmail());
        userProfile.setUsername(user.getUsername());
        userProfile.setFollowers(user.getFollower().stream().map(User::getId).collect(Collectors.toList()));
        userProfile.setTweets(user.getTweets().stream().map(Tweet::getId).collect(Collectors.toList()));
        return userProfile;
    }
}
