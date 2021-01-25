package ir.ac.sbu.twitter.repository;

import ir.ac.sbu.twitter.model.LikesLog;
import ir.ac.sbu.twitter.model.RetweetLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetweetLogRepository extends JpaRepository<RetweetLog, Long> {

    RetweetLog save(RetweetLog s);
    List<RetweetLog> findAllByUserId(long userId);
    List<RetweetLog> findAllByTweetId(long TweetId);
}
