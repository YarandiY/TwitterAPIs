package ir.ac.sbu.twitter.service;

import ir.ac.sbu.twitter.dto.*;
import ir.ac.sbu.twitter.exception.DuplicateInputError;
import ir.ac.sbu.twitter.exception.InvalidInput;
import ir.ac.sbu.twitter.model.FollowLog;
import ir.ac.sbu.twitter.model.Tweet;
import ir.ac.sbu.twitter.model.User;
import ir.ac.sbu.twitter.repository.FollowLogRepository;
import ir.ac.sbu.twitter.repository.UserRepository;
import ir.ac.sbu.twitter.security.JwtTokenProvider;
import ir.ac.sbu.twitter.security.UserDetailsServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TweetService tweetService;

    @Autowired
    private FollowLogRepository followLogRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    private static final Logger logger = LogManager.getLogger();

    private final AuthenticationManager authenticationManager;

    public UserService(PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    public UserDto changeUsername(String username) throws InvalidInput {
        User user = findUser();
        boolean usernameExist = userRepository.findByUsername(username).isPresent();
        if (!user.getUsername().equals(username) && usernameExist)
            throw new InvalidInput("username exist");
        user.setUsername(username);
        userRepository.save(user);
        return getDto(user);
    }

    public User create(UserCreate userCreate) throws DuplicateInputError {
        if (userRepository.findByEmail(userCreate.getEmail()).isPresent())
            throw new DuplicateInputError();
        if (userRepository.findByUsername(userCreate.getUsername()).isPresent())
            throw new DuplicateInputError();
        User user = new User();
        user.setEmail(userCreate.getEmail());
        user.setPassword(passwordEncoder.encode(userCreate.getPassword()));
        user.setUsername(userCreate.getUsername());
        user.setPicture("/show/pic/default_profile_picture.jpg");
        return userRepository.save(user);
    }

    public JwtAuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        User user = (User) authentication.getPrincipal();
        logger.info("User with [username: {}] has logged in", user.getUsername());
        return new JwtAuthenticationResponse("Bearer " + jwt);
    }

    public void follow(String username) throws InvalidInput {
        User following = userRepository.findByUsername(username).orElseThrow(
                () -> new InvalidInput("the username doesnt exist"));
        User follower = findUser();
        if (follower.equals(following))
            return;
        List<User> followings = follower.getFollowings();
        if (followings == null)
            followings = new ArrayList<>();
        if (followings.contains(following)) {
            unfollow(follower, following);
            return;
        }
        followings.add(following);
        follower.setFollowings(followings);
        userRepository.save(follower);
        FollowLog followLog = new FollowLog();
        followLog.setDate(new Date());
        followLog.setFollowingId(following.getId());
        followLog.setUserId(follower.getId());
        followLogRepository.save(followLog);
    }

    public void unfollow(User follower, User following) {
        List<User> followings = follower.getFollowings();
        if (!followings.contains(following))
            return;
        followings.remove(following);
        follower.setFollowings(followings);
        userRepository.save(follower);
    }

    public User findUser() throws InvalidInput {
        return userRepository.findByUsername(userDetailsService.getUser().getUsername()).orElseThrow(
                () -> new InvalidInput("the token is expired"));
    }

    public List<Long> getFollowings(String username) throws InvalidInput {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new InvalidInput("the username doesnt exist")
        );
        return user.getFollowings().stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    public List<Long> getFollowers(String username) throws InvalidInput {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new InvalidInput("the username doesnt exist")
        );
        return userRepository.findAllByFollowingsContaining(user)
                .stream().map(User::getId)
                .collect(Collectors.toList());
    }

    public User get(long userId) throws InvalidInput {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent())
            throw new InvalidInput("the id doesnt exist");
        return optionalUser.get();
    }

    public List<TweetDto> getTweets(String username) throws InvalidInput {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new InvalidInput("the username doesnt exist")
        );
        User viewer = userDetailsService.getUser();
        return user.getTweets().stream().map(t -> {
            try {
                boolean isLiked = viewer != null && t.getLiked().contains(viewer);
                boolean isRetweeted = viewer != null && t.getRetweets().contains(viewer);
                return t.getDto(getDto(t.getAuthorId()), isLiked, isRetweeted);
            } catch (InvalidInput invalidInput) {
                logger.error("something went wrong!");
                return null;
            }
        })
                .sorted(Comparator.comparing(TweetDto::getDate).reversed())
                .collect(Collectors.toList());
    }

    public UserDto getDto(long userId) throws InvalidInput {
        User user = get(userId);
        return getDto(user);
    }

    public UserDto getDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setPicture(user.getPicture());
        User viewer = userDetailsService.getUser();
        try {
            userDto.setFollowed(viewer != null && getFollowings(viewer.getUsername()).contains(user.getId()));
        } catch (InvalidInput ignored) {
        }
        return userDto;
    }

    public List<TweetDto> likedTweets(String username) throws InvalidInput {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new InvalidInput("the username doesnt exist")
        );
        User viewer = userDetailsService.getUser();
        return tweetService.getLiked(user)
                .stream().map(t -> {
                    try {
                        boolean isLiked = viewer != null && t.getLiked().contains(viewer);
                        boolean isRetweeted = viewer != null && t.getRetweets().contains(viewer);
                        return t.getDto(getDto(t.getAuthorId()), isLiked, isRetweeted);
                    } catch (InvalidInput invalidInput) {
                        logger.error("something went wrong!");
                        return null;
                    }
                })
                .sorted(Comparator.comparing(TweetDto::getDate).reversed())
                .collect(Collectors.toList());
    }

    public List<TweetDto> getTimeline() throws InvalidInput {
        User user = findUser();
        List<Tweet> tweets = user.getTweets();
        tweets.addAll(user.getFollowings()
                .stream().map(User::getTweets)
                .flatMap(List::stream).collect(Collectors.toList()));
        return tweets.stream()
                .map(t -> {
                    try {
                        boolean isLiked = t.getLiked().contains(user);
                        boolean isRetweeted = t.getRetweets().contains(user);
                        return t.getDto(getDto(t.getAuthorId()), isLiked, isRetweeted);
                    } catch (InvalidInput invalidInput) {
                        logger.error("something went wrong!");
                        return null;
                    }
                })
                .collect(Collectors.toSet()).stream()
                .sorted(Comparator.comparing(TweetDto::getDate).reversed())
                .collect(Collectors.toList());
    }

    public List<UserDto> search(String username) {
        return userRepository.findAllByUsernameContaining(username)
                .stream().map(this::getDto).collect(Collectors.toList());
    }
}
