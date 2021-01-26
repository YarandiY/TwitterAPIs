package ir.ac.sbu.twitter.controller;


import ir.ac.sbu.twitter.dto.LogDto;
import ir.ac.sbu.twitter.dto.TweetDto;
import ir.ac.sbu.twitter.exception.InvalidInput;
import ir.ac.sbu.twitter.service.PictureService;
import ir.ac.sbu.twitter.service.TweetService;
import ir.ac.sbu.twitter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.multipart.MultipartFile;


import javax.imageio.ImageIO;
import javax.websocket.server.PathParam;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/")
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private TweetService tweetService;

    @Autowired
    private PictureService pictureService;

    @GetMapping(value = "/timeline")
    public ResponseEntity<List<TweetDto>> get() {
        try {
            List<TweetDto> tweets = userService.getTimeline();
            return new ResponseEntity<>(tweets, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List> search(@PathParam("input") String input){
        input = input.replaceFirst("\\$","#");
        String[] words = input.split(" ");
        if (words[0].startsWith("@"))
            return new ResponseEntity<>(
                    userService.search(words[0].substring(1))
                    ,HttpStatus.OK);
        if (words[0].startsWith("#"))
                return new ResponseEntity<>(
                        tweetService.searchByHashtags(words[0])
                        ,HttpStatus.OK);
        else
                return new ResponseEntity<>(
                        tweetService.searchBody(input)
                        ,HttpStatus.OK);
    }

    @GetMapping(value = "/notifications")
    public ResponseEntity<List<LogDto>> notifications(){
        try {
            List<LogDto> result = tweetService.getLogs();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (InvalidInput invalidInput) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/show/pic/{path}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity show(@PathVariable String path) throws IOException {
        byte[] data = pictureService.show(path);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path + "\"")
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }
}
