package ir.ac.sbu.twitter.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Tweet {
    private String body;
    private Date date;
    private User author;
    private List<Hashtag> hashtags;
}
