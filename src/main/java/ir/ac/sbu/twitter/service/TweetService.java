package ir.ac.sbu.twitter.service;

import ir.ac.sbu.twitter.dto.*;
import ir.ac.sbu.twitter.exception.InvalidInput;
import ir.ac.sbu.twitter.model.*;
import ir.ac.sbu.twitter.repository.*;
import ir.ac.sbu.twitter.security.UserDetailsServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private LikesLogRepository likesLogRepository;
    @Autowired
    private RetweetLogRepository retweetLogRepository;
    @Autowired
    private FollowLogRepository followLogRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HashtagRepository hashtagRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LogManager.getLogger();

    public List<Tweet> getLiked(User user){
        return tweetRepository.getAllByLikedContaining(user);
    }

    public Tweet get(long tweetId) throws InvalidInput {
        Optional<Tweet> optionalTweet = tweetRepository.findById(tweetId);
        if(!optionalTweet.isPresent())
            throw new InvalidInput("the id doesnt exist");
        return optionalTweet.get();
    }

    public TweetDto getDto(long id) throws InvalidInput {
        User user = userDetailsService.getUser();
        Tweet t = get(id);
        boolean isLiked = user !=null && t.getLiked().contains(user);
        boolean isRetweeted = user !=null && t.getRetweets().contains(user);
        return t.getDto(userService
                .getDto(t.getAuthorId()),
                isLiked,
                isRetweeted);
    }

    public TweetDto add(String body) throws InvalidInput {
        if(body.length() > 250)
            throw new InvalidInput("the length of the body is too large");
        User author = userService.findUser();
        UserDto authorDto = userService.getDto(author);
        Tweet tweet = new Tweet();
        tweet.setBody(body);
        tweet.setHashtags(findHashtags(body));
        tweet.setDate(new Date());
        tweet.setAuthorId(author.getId());
        tweet = tweetRepository.save(tweet);
        TweetDto tweetDto = tweet.getDto(authorDto, false, false);
        List<Tweet> tweets = author.getTweets();
        if(tweets == null)
            tweets = new ArrayList<>();
        tweets.add(tweet);
        author.setTweets(tweets);
        userRepository.save(author);
        return tweetDto;
    }

    public List<Hashtag> findHashtags(String doc){
        List<String> stream = List.of(doc.split(" "))
                .stream().filter(w -> w.contains("#"))
                .map(w -> w.substring(w.indexOf("#"))).collect(Collectors.toList());
        List<Hashtag> result = new ArrayList<>();
        for (String str :
                stream) {
            Hashtag hashtag = new Hashtag();
            hashtag.setBody(str);
            Optional<Hashtag> optionalHashtag = hashtagRepository.findByBody(str);
            if(!optionalHashtag.isPresent())
                hashtag = hashtagRepository.save(hashtag);
            else
                hashtag = optionalHashtag.get();
            result.add(hashtag);
        }
        return result;
    }

    public boolean delete(long tweetId) throws InvalidInput {
        Tweet tweet = get(tweetId);
        if(!checkAccess(tweet))
            return false;
        List<User> users = userRepository.findAllByTweetsContaining(tweet);
        for (User u :
                users) {
            List<Tweet> t = u.getTweets();
            while (t.contains(tweet)) t.remove(tweet);
            u.setTweets(t);
            userRepository.save(u);
        }
        tweetRepository.delete(tweet);
        return true;
    }

    public boolean like(long tweetId) throws InvalidInput {
        Tweet tweet = get(tweetId);
        User user = userService.findUser();
        List<User> liked = tweet.getLiked();
        if(liked == null){
            liked = new ArrayList<>();
            liked.add(user);
            LikesLog likesLog = new LikesLog();
            likesLog.setTweetId(tweetId);
            likesLog.setUserId(user.getId());
            likesLog.setDate(new Date());
            likesLogRepository.save(likesLog);
        }
        else if(liked.contains(user))
            liked.remove(user);
        else{
            liked.add(user);
            LikesLog likesLog = new LikesLog();
            likesLog.setTweetId(tweetId);
            likesLog.setUserId(user.getId());
            likesLog.setDate(new Date());
            likesLogRepository.save(likesLog);
        }
        tweet.setLiked(liked);
        tweetRepository.save(tweet);
        return true;
    }

    public boolean checkAccess(Tweet tweet) throws InvalidInput {
        User user = userService.findUser();
        if(tweet.getAuthorId() != user.getId())
            return false;
        return true;
    }

    public boolean retweet(long tweetId) throws InvalidInput {
        User user = userService.findUser();
        Tweet tweet = get(tweetId);
        List<User> rts = tweet.getRetweets();
        List<Tweet> tweets = user.getTweets();
        if(rts == null){
            rts = new ArrayList<>();
            rts.add(user);
            tweets.add(tweet);
            RetweetLog retweetLogLog = new RetweetLog();
            retweetLogLog.setTweetId(tweetId);
            retweetLogLog.setUserId(user.getId());
            retweetLogLog.setDate(new Date());
            retweetLogRepository.save(retweetLogLog);
        }
        else if(rts.contains(user)){
            rts.remove(user);
            tweets.remove(tweet);
        }
        else{
            rts.add(user);
            tweets.add(tweet);
            RetweetLog retweetLogLog = new RetweetLog();
            retweetLogLog.setTweetId(tweetId);
            retweetLogLog.setUserId(user.getId());
            retweetLogLog.setDate(new Date());
            retweetLogRepository.save(retweetLogLog);
        }
        tweet.setRetweets(rts);
        tweetRepository.save(tweet);
        user.setTweets(tweets);
        userRepository.save(user);
        return true;
    }

    public List<TweetDto> searchByHashtags(String hashtag) {
        User user = userDetailsService.getUser();
        Hashtag ht = new Hashtag();
        ht.setBody(hashtag);
        return tweetRepository.findAll()
                .stream().filter(t -> t.getHashtags().contains(ht))
                .map(t -> {
                    try {
                        boolean isLiked = user !=null && t.getLiked().contains(user);
                        boolean isRetweeted = user !=null && t.getRetweets().contains(user);
                        return t.getDto(userService.getDto(t.getAuthorId()), isLiked, isRetweeted);
                    } catch (InvalidInput invalidInput) {
                        logger.error("something went wrong!");
                        return null;
                    }
                })
                .sorted(Comparator.comparing(TweetDto::getDate).reversed())
                .collect(Collectors.toList());
    }

    public List<TweetDto> searchBody(String input){ //TODO
        User user = userDetailsService.getUser();
        return tweetRepository.findAllByBodyContaining(input).stream()
                .map(t -> {
                    try {
                        boolean isLiked = user !=null && t.getLiked().contains(user);
                        boolean isRetweeted = user !=null && t.getRetweets().contains(user);
                        return t.getDto(userService.getDto(t.getAuthorId()), isLiked, isRetweeted);
                    } catch (InvalidInput invalidInput) {
                        logger.error("something went wrong!");
                        return null;
                    }
                })
                .sorted(Comparator.comparing(TweetDto::getDate).reversed())
                .collect(Collectors.toList());
    }

    public List<LogDto> getLogs() throws InvalidInput {
        User user = userService.findUser();
        List<Tweet> tweets = user.getTweets();
        List<User> followings = user.getFollowings();
        List<LogDto> result = new ArrayList<>();
        List<Log> logs = new ArrayList<>();
        for (Tweet t :
                tweets){
            logs.addAll(likesLogRepository.findAllByTweetId(t.getId()));
            logs.addAll(retweetLogRepository.findAllByTweetId(t.getId()));
        }
        for (User u :
                followings){
            logs.addAll(likesLogRepository.findAllByUserId(u.getId()));
            logs.addAll(retweetLogRepository.findAllByUserId(u.getId()));
            logs.addAll(followLogRepository.findAllByUserId(u.getId()));
        }
        result.addAll(logs.stream()
                .map(ll -> {
                    LogDto dto = new LogDto();
                    dto.setDate(ll.getDate());
                    try {
                        if(ll.getFollowingId() != -1)
                            dto.setFollowing(userService.getDto(ll.getFollowingId()));
                        dto.setDoer(userService.getDto(ll.getUserId()));
                        dto.setTweetId(ll.getTweetId());
                        if(ll.getTweetId() != -1)
                            dto.setTweetBody(get(ll.getTweetId()).getBody());
                    } catch (InvalidInput invalidInput) {
                        logger.error("token is invalid!");
                    }
                    if(ll instanceof LikesLog)
                        dto.setType(TypeLog.like);
                    else if(ll instanceof RetweetLog)
                        dto.setType(TypeLog.retweet);
                    else
                        dto.setType(TypeLog.follow);
                    return dto;
                })
                .collect(Collectors.toSet()).stream()
                .sorted(Comparator.comparing(LogDto::getDate).reversed())
                .collect(Collectors.toList())
        );
        return result;
    }

    public List<UserDto> getLikes(long id) throws InvalidInput {
        Tweet tweet = get(id);
        List<UserDto> result = tweet.getLiked().stream()
                .map(u -> userService.getDto(u))
                .collect(Collectors.toList());
        return result;
    }

    public List<UserDto> getRetweets(long id) throws InvalidInput {
        Tweet tweet = get(id);
        List<UserDto> result = tweet.getRetweets().stream()
                .map(u -> userService.getDto(u))
                .collect(Collectors.toList());
        return result;
    }
}
