package ir.ac.sbu.twitter.repository;

import ir.ac.sbu.twitter.model.LikesLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface LikesLogRepository extends JpaRepository<LikesLog, Long> {

    LikesLog save(LikesLog s);
    List<LikesLog> findAllByUserId(long userId);
    List<LikesLog> findAllByTweetId(long TweetId);
}
