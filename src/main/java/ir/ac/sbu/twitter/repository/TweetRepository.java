package ir.ac.sbu.twitter.repository;

import ir.ac.sbu.twitter.model.Tweet;
import ir.ac.sbu.twitter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

    Optional<Tweet> findById(Long id);
    List<Tweet> getAllByLikedContaining(User user);
}
