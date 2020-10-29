package com.example.demo.controllers;

import com.example.demo.SareetaApplication;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Transactional
@SpringBootTest(classes = SareetaApplication.class)
public class UserControllerTest {
    private final BCryptPasswordEncoder bCryptPasswordEncoder = Mockito.mock(BCryptPasswordEncoder.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserController userController;

    @Test
    public void createUser() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("user1");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");


    }


}
