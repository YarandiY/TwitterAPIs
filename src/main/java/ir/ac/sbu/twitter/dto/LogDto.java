package ir.ac.sbu.twitter.dto;

import lombok.Data;

import java.util.Date;

@Data
public class LogDto {
    private TypeLog type;
    private UserDto doer;
    private UserDto following;
    private String tweet;
    private Date date;
}
