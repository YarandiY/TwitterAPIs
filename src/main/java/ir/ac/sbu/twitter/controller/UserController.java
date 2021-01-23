package ir.ac.sbu.twitter.controller;

import ir.ac.sbu.twitter.UserDTO.UserCreate;
import ir.ac.sbu.twitter.UserDTO.UserProfile;
import ir.ac.sbu.twitter.exception.DuplicateInputError;
import ir.ac.sbu.twitter.exception.InvalidInput;
import ir.ac.sbu.twitter.model.User;
import ir.ac.sbu.twitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<UserProfile> get(@PathVariable long id) {
        try {
            UserProfile user = userService.getProfile(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
