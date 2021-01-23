package ir.ac.sbu.twitter.UserDTO;

import lombok.Data;

@Data
public class UserCreate {
    private String username;
    private String email;
    private String password;
}
