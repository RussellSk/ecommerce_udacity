package com.example.demo.configuration;

import com.example.demo.model.persistence.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImplementor implements UserDetailsService {
    private final UserRepository applicationUserRepository;
    public UserDetailsServiceImplementor(UserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.demo.model.persistence.User authUser = applicationUserRepository.findByUsername(username);
        if (authUser == null) throw new UsernameNotFoundException(username);
        return new org.springframework.security.core.userdetails.User(authUser.getUsername(), authUser.getPassword(), new ArrayList<>());
    }
}