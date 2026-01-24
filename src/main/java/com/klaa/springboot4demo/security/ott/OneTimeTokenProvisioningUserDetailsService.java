package com.klaa.springboot4demo.security.ott;

import com.klaa.springboot4demo.user.User;
import com.klaa.springboot4demo.user.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class OneTimeTokenProvisioningUserDetailsService implements UserDetailsService {
    UserService userService;
    ConversionService conversionService;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        val user = userService.findByEmail(email).orElseGet(()->saveUser(email));


        return  org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password("")
                .authorities(List.of())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
    private User saveUser(String email){
        return userService.saveUser(email);
    }

}
