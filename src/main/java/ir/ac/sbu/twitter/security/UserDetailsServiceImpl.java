package ir.ac.sbu.twitter.security;

import ir.ac.sbu.twitter.exception.InvalidInput;
import ir.ac.sbu.twitter.model.User;
import ir.ac.sbu.twitter.repository.UserRepository;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Getter
    private User user;

    @SneakyThrows
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new InvalidInput("User not found [username: " + username + "]")
                );
        return user;
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) throws InvalidInput {
        user = userRepository.findById(id).orElseThrow(
                () -> new InvalidInput("User not found [id: " + id + "]"));
        return  user;
    }
}
