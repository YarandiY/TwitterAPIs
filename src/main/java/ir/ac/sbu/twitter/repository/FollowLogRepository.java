package ir.ac.sbu.twitter.repository;

import ir.ac.sbu.twitter.model.FollowLog;
import ir.ac.sbu.twitter.model.LikesLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowLogRepository extends JpaRepository<FollowLog, Long> {

    FollowLog save(FollowLog s);
    List<FollowLog> findAllByUserId(long userId);
    List<FollowLog> findAllByFollowingId(long followingId);
}
