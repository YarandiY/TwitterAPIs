package ir.ac.sbu.twitter.service;

import ir.ac.sbu.twitter.dto.TweetCreate;
import ir.ac.sbu.twitter.dto.TweetDto;
import ir.ac.sbu.twitter.dto.UserDto;
import ir.ac.sbu.twitter.exception.InvalidInput;
import ir.ac.sbu.twitter.model.Tweet;
import ir.ac.sbu.twitter.model.User;
import ir.ac.sbu.twitter.repository.TweetRepository;
import ir.ac.sbu.twitter.repository.UserRepository;
import ir.ac.sbu.twitter.security.UserDetailsServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

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

    public TweetDto add(TweetCreate tweetCreate){
        User author = userRepository.findByUsername(userDetailsService.getUser().getUsername()).orElseThrow(
                () -> new RuntimeException("the token is expired")
        );
        UserDto authorDto = new UserDto();
        authorDto.setUsername(author.getUsername());
        Tweet tweet = new Tweet();
        tweet.setBody(tweetCreate.getBody());
        tweet.setHashtags(findHashtags(tweetCreate.getBody()));
        tweet.setDate(new Date());
        tweet.setAuthorId(author.getId());
        tweet = tweetRepository.save(tweet);
        TweetDto tweetDto = tweet.getDto(authorDto);
        return tweetDto;
    }

    public List<String> findHashtags(String doc){
        return List.of(doc.split(" "))
                .stream().filter(w -> w.startsWith("#"))
                .collect(Collectors.toList());
    }

}
