package ir.ac.sbu.twitter.controller;

import ir.ac.sbu.twitter.dto.*;
import ir.ac.sbu.twitter.dto.TweetDto;
import ir.ac.sbu.twitter.dto.UserCreate;
import ir.ac.sbu.twitter.dto.UserDto;
import ir.ac.sbu.twitter.exception.DuplicateInputError;
import ir.ac.sbu.twitter.exception.InvalidInput;
import ir.ac.sbu.twitter.model.User;
import ir.ac.sbu.twitter.security.UserDetailsServiceImpl;
import ir.ac.sbu.twitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<Long> create(@RequestBody UserCreate user) {
        try {
            User user1 = userService.create(user);
            return new ResponseEntity<>(user1.getId(), HttpStatus.OK);
        } catch (DuplicateInputError duplicateInputError) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/login")
    private ResponseEntity<JwtAuthenticationResponse> login(@RequestBody LoginRequest loginRequest){
        JwtAuthenticationResponse token = userService.login(loginRequest);
        return new ResponseEntity<>(token, HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/{id}/get")
    public ResponseEntity<UserDto> get(@PathVariable long id) {
        try {
            UserDto user = userService.getDto(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/me")
    public ResponseEntity<UserDto> get() {
        UserDto userDto = new UserDto();
        userDto.setUsername(userDetailsService.getUser().getUsername());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping(value = "/follow")
    public ResponseEntity<Boolean> follow(@RequestBody FollowRequest request){
        System.err.println(request.getUsername());
        try {
            userService.follow(request.getUsername());
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        }catch (InvalidInput invalidInput){
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/followers")
    public ResponseEntity<List<String>> getFollowers() {
        try {
            List<String> followers = userService.getFollowers();
            return new ResponseEntity<>(followers, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/followings")
    public ResponseEntity<List<String>> getFollowings() {
        try {
            List<String> followers = userService.getFollowings();
            return new ResponseEntity<>(followers, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/tweets")
    public ResponseEntity<List<TweetDto>> getTweets() {
        try {
            List<TweetDto> followers = userService.getTweets();
            return new ResponseEntity<>(followers, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/liked")
    public ResponseEntity<List<TweetDto>> getLiked() {
        try {
            List<TweetDto> followers = userService.likedTweets();
            return new ResponseEntity<>(followers, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

}
