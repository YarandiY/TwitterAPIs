package ir.ac.sbu.twitter.repository;

import ir.ac.sbu.twitter.model.Tweet;
import ir.ac.sbu.twitter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByFollowingsContaining(User u);
    List<User> findAllByTweetsContaining(Tweet t);
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String username);
    User save(User s);
}
