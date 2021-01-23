package ir.ac.sbu.twitter.controller;

import ir.ac.sbu.twitter.dto.TweetDto;
import ir.ac.sbu.twitter.dto.UserCreate;
import ir.ac.sbu.twitter.dto.UserDto;
import ir.ac.sbu.twitter.exception.DuplicateInputError;
import ir.ac.sbu.twitter.exception.InvalidInput;
import ir.ac.sbu.twitter.model.User;
import ir.ac.sbu.twitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<Long> create(@RequestBody UserCreate user) {
        try {
            User user1 = userService.create(user);
            return new ResponseEntity<>(user1.getId(), HttpStatus.OK);
        } catch (DuplicateInputError duplicateInputError) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}/get")
    public ResponseEntity<UserDto> get(@PathVariable long id) {
        try {
            UserDto user = userService.getDto(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}/followers")
    public ResponseEntity<List<String>> getFollowers(@PathVariable long id) {
        try {
            List<String> followers = userService.getFollowers(id);
            return new ResponseEntity<>(followers, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}/followings")
    public ResponseEntity<List<String>> getFollowings(@PathVariable long id) {
        try {
            List<String> followers = userService.getFollowings(id);
            return new ResponseEntity<>(followers, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}/tweets")
    public ResponseEntity<List<TweetDto>> getTweets(@PathVariable long id) {
        try {
            List<TweetDto> followers = userService.getTweets(id);
            return new ResponseEntity<>(followers, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}/liked")
    public ResponseEntity<List<TweetDto>> getLiked(@PathVariable long id) {
        try {
            List<TweetDto> followers = userService.likedTweets(id);
            return new ResponseEntity<>(followers, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

}
