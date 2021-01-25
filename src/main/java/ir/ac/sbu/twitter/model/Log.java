package ir.ac.sbu.twitter.model;

import java.util.Date;

public interface Log {
    Date getDate();
    long getUserId();
    long getTweetId();
    long getFollowingId();
}
