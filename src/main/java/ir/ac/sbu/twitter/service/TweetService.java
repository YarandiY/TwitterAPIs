package ir.ac.sbu.twitter.service;

import ir.ac.sbu.twitter.dto.TweetCreate;
import ir.ac.sbu.twitter.dto.TweetDto;
import ir.ac.sbu.twitter.dto.UserDto;
import ir.ac.sbu.twitter.exception.InvalidInput;
import ir.ac.sbu.twitter.model.Tweet;
import ir.ac.sbu.twitter.model.User;
import ir.ac.sbu.twitter.repository.TweetRepository;
import ir.ac.sbu.twitter.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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

    public TweetDto add(TweetCreate tweetCreate) throws InvalidInput {
        User author = userService.findUser();
        UserDto authorDto = new UserDto();
        authorDto.setUsername(author.getUsername());
        Tweet tweet = new Tweet();
        tweet.setBody(tweetCreate.getBody());
        tweet.setHashtags(findHashtags(tweetCreate.getBody()));
        tweet.setDate(new Date());
        tweet.setAuthorId(author.getId());
        tweet = tweetRepository.save(tweet);
        TweetDto tweetDto = tweet.getDto(authorDto);
        List<Tweet> tweets = author.getTweets();
        if(tweets == null)
            tweets = new ArrayList<>();
        tweets.add(tweet);
        author.setTweets(tweets);
        userRepository.save(author);
        return tweetDto;
    }

    public List<String> findHashtags(String doc){
        return List.of(doc.split(" "))
                .stream().filter(w -> w.startsWith("#"))
                .collect(Collectors.toList());
    }

    public boolean delete(long tweetId) throws InvalidInput {
        Tweet tweet = get(tweetId);
        if(!checkAccess(tweet))
            return false;
        List<User> users = userRepository.findAll();
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
        }
        else if(liked.contains(user))
            liked.remove(user);
        else
            liked.add(user);
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
        }
        else if(rts.contains(user)){
            rts.remove(user);
            tweets.remove(tweet);
        }
        else{
            rts.add(user);
            tweets.add(tweet);
        }
        tweet.setRetweets(rts);
        tweetRepository.save(tweet);
        user.setTweets(tweets);
        userRepository.save(user);
        return true;
    }
}
