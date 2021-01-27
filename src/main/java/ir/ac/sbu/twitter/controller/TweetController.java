package ir.ac.sbu.twitter.controller;

import ir.ac.sbu.twitter.dto.TweetDto;
import ir.ac.sbu.twitter.dto.UserDto;
import ir.ac.sbu.twitter.exception.InvalidInput;
import ir.ac.sbu.twitter.service.PictureService;
import ir.ac.sbu.twitter.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/tweets")
public class TweetController {

    @Autowired
    private TweetService tweetService;

    @Autowired
    private PictureService pictureService;

    @PostMapping(value = "/add")
    public ResponseEntity<TweetDto> create(@RequestPart @Nullable String body, @RequestPart @Nullable MultipartFile picture) {
        System.out.println("test " + body);
        if((body == null || body.length() < 1) && (picture == null || picture.isEmpty()))
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        if(body == null)  body = "";
        try {
            TweetDto dto = tweetService.add(body);
            if (picture != null) dto = pictureService.addPicture(dto, picture);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable long id) {
        try {
            boolean result = tweetService.delete(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/like/{id}")
    public ResponseEntity<Boolean> like(@PathVariable long id) {
        try {
            boolean result = tweetService.like(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/retweet/{id}")
    public ResponseEntity<Boolean> retweet(@PathVariable long id) {
        try {
            boolean result = tweetService.retweet(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<TweetDto> get(@PathVariable long id) {
        try {
            TweetDto dto = tweetService.getDto(id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getLikes/{id}")
    public ResponseEntity<List<UserDto>> getLikes(@PathVariable long id) {
        try {
            List<UserDto> dto = tweetService.getLikes(id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/getRetweets/{id}")
    public ResponseEntity<List<UserDto>> getRetweets(@PathVariable long id) {
        try {
            List<UserDto> dto = tweetService.getRetweets(id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
