package com.himanshu.Authifyy.service;


import com.himanshu.Authifyy.entity.UserEntity;
import com.himanshu.Authifyy.repositry.UserRepositry;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepositry userRepositry;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity existingUser= userRepositry.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Email not found for the: "+email));
        return new User(existingUser.getEmail(),existingUser.getPassword(),new ArrayList<>());
    }

}
