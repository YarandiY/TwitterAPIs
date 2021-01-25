package ir.ac.sbu.twitter.controller;

import ir.ac.sbu.twitter.dto.TweetCreate;
import ir.ac.sbu.twitter.dto.TweetDto;
import ir.ac.sbu.twitter.dto.UserCreate;
import ir.ac.sbu.twitter.exception.DuplicateInputError;
import ir.ac.sbu.twitter.exception.InvalidInput;
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
        try {
            TweetDto dto = tweetService.add(tweetCreate);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable long id){
        try{
            boolean result = tweetService.delete(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (InvalidInput invalidInput){
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/like/{id}")
    public ResponseEntity<Boolean> like(@PathVariable long id){
        try{
            boolean result = tweetService.like(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (InvalidInput invalidInput){
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/retweet/{id}")
    public ResponseEntity<Boolean> retweet(@PathVariable long id){
        try{
            boolean result = tweetService.retweet(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (InvalidInput invalidInput){
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }
}
