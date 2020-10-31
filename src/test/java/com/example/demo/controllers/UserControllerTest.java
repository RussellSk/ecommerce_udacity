package com.example.demo.controllers;

import com.example.demo.SareetaApplication;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SareetaApplication.class)
public class UserControllerTest {
    @Autowired
    private UserController userController;

    @Test
    public void createUser() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("user1");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");
        ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(200, responseEntity.getStatusCodeValue());
        User createdUser = responseEntity.getBody();
        Assert.assertNotNull(createdUser);
        Assert.assertEquals(createUserRequest.getUsername(), createdUser.getUsername());
        Assert.assertTrue(createdUser.getId() > 0);
    }

    @Test
    public void findById() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("userTest");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");
        ResponseEntity<User> createUserResponse = userController.createUser(createUserRequest);
        Assert.assertEquals(200, createUserResponse.getStatusCodeValue());
        Assert.assertNotNull(createUserResponse);
        User createdUserModel = createUserResponse.getBody();

        ResponseEntity<User> responseEntity = userController.findById(1L);
        Assert.assertEquals(200, responseEntity.getStatusCodeValue());
        Assert.assertNotNull(responseEntity);
        User foundUser = responseEntity.getBody();
        Assert.assertNotNull(foundUser);
        Assert.assertEquals(1L, foundUser.getId());
    }

    @Test
    public void findByUserName() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("userTest");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");
        ResponseEntity<User> createUserResponse = userController.createUser(createUserRequest);
        Assert.assertEquals(200, createUserResponse.getStatusCodeValue());
        Assert.assertNotNull(createUserResponse);
        User createdUserModel = createUserResponse.getBody();

        ResponseEntity<User> responseEntity = userController.findByUserName(createUserRequest.getUsername());
        Assert.assertEquals(200, responseEntity.getStatusCodeValue());
        Assert.assertNotNull(responseEntity);
        User foundUser = responseEntity.getBody();
        Assert.assertNotNull(foundUser);
        Assert.assertEquals(createUserRequest.getUsername(), foundUser.getUsername());
    }
}
