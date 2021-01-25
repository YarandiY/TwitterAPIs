package ir.ac.sbu.twitter.controller;


import ir.ac.sbu.twitter.dto.TweetDto;
import ir.ac.sbu.twitter.dto.UserDto;
import ir.ac.sbu.twitter.exception.InvalidInput;
import ir.ac.sbu.twitter.model.Tweet;
import ir.ac.sbu.twitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/")
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/timeline")
    public ResponseEntity<List<TweetDto>> get() {
        List<TweetDto> tweets = userService.getTimeline();
        return new ResponseEntity<>(tweets, HttpStatus.OK);
    }
}
