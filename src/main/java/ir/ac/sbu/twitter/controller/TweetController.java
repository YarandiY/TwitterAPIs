package ir.ac.sbu.twitter.controller;

import ir.ac.sbu.twitter.dto.TweetCreate;
import ir.ac.sbu.twitter.dto.TweetDto;
import ir.ac.sbu.twitter.dto.UserCreate;
import ir.ac.sbu.twitter.exception.DuplicateInputError;
import ir.ac.sbu.twitter.model.User;
import ir.ac.sbu.twitter.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/tweets")
public class TweetController {

    @Autowired
    private TweetService tweetService;

    @PostMapping(value = "/add", consumes = "application/json")
    public ResponseEntity<TweetDto> create(@RequestBody TweetCreate tweetCreate) {
        TweetDto dto = tweetService.add(tweetCreate);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}
