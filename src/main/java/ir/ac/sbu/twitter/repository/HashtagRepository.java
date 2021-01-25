package ir.ac.sbu.twitter.repository;


import ir.ac.sbu.twitter.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Hashtag save(Hashtag s);
    Optional<Hashtag> findByBody(String body);
}
