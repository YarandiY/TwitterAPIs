package ir.ac.sbu.twitter.dto;

import lombok.Data;

@Data
public class UserCreate {
    private String username;
    private String email;
    private String password;
}
