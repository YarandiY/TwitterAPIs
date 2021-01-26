package ir.ac.sbu.twitter.service;

import ir.ac.sbu.twitter.dto.UserDto;
import ir.ac.sbu.twitter.exception.InvalidInput;
import ir.ac.sbu.twitter.model.User;
import ir.ac.sbu.twitter.repository.UserRepository;
import ir.ac.sbu.twitter.security.UserDetailsServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

@Service
public class PictureService {

    private static final String IMAGE_PATH = "images/";
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    public void save(MultipartFile pic, String path) throws IOException {
        logger.info("start to try save picture with path: " + path);
        File upl = new File(path);
        upl.createNewFile();
        FileOutputStream fout = new FileOutputStream(upl);
        BufferedImage bufferedImage = ImageIO.read(pic.getInputStream());
        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName("jpg");
        if (!imageWriters.hasNext()) {
            throw new IllegalStateException("Writers Not Found!!");
        }
        ImageWriter imageWriter = imageWriters.next();
        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(fout);
        imageWriter.setOutput(imageOutputStream);
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        if(pic.getSize() > 3000000){
            imageWriteParam.setCompressionQuality(0.5F);
        }else{
            imageWriteParam.setCompressionQuality(1.0F);
        }
        imageWriter.write(null, new IIOImage(bufferedImage, null, null), imageWriteParam);
        fout.close();
        imageOutputStream.close();
        imageWriter.dispose();
        logger.info("picture saved");
    }


    public UserDto setProfilePicture(MultipartFile picture) throws InvalidInput {
        User user = userService.findUser();
        long userId = user.getId();
        String fileName = "profile_" + userId + ".jpg";
        String pathName = IMAGE_PATH + fileName;
        try {
            save(picture, pathName);
            user.setPicture("/show/pic/" + fileName);
            userRepository.save(user);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return userService.getDto(userId);
    }

    public byte[] show(String filename) throws IOException {
        String path = IMAGE_PATH + filename;
        System.err.println(path);
        BufferedImage bImage = ImageIO.read(new File(path));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpg", bos );
        return bos.toByteArray();
    }
}
