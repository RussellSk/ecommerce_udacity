package com.example.demo.controllers;

import com.example.demo.SareetaApplication;
import com.example.demo.SareetaApplicationTests;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SareetaApplication.class)
public class CartControllerTest {

    @Autowired
    private CartController cartController;

    @Autowired
    private UserController userController;

    private ItemRepository itemRepository = mock(ItemRepository.class);


    @Test
    public void addToCart() {
        when(itemRepository.findAll()).thenReturn(createItemsList());

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("user1");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");
        ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(200, responseEntity.getStatusCodeValue());

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername(createUserRequest.getUsername());
        ResponseEntity<Cart> cartResponseEntity =  cartController.addTocart(modifyCartRequest);
        Assert.assertEquals(200, cartResponseEntity.getStatusCodeValue());
    }

    @Test
    public void removeFromCart() {
        when(itemRepository.findAll()).thenReturn(createItemsList());
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("user1");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");
        ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(200, responseEntity.getStatusCodeValue());

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername(createUserRequest.getUsername());
        ResponseEntity<Cart> cartResponseEntity =  cartController.addTocart(modifyCartRequest);
        Assert.assertEquals(200, cartResponseEntity.getStatusCodeValue());

        ResponseEntity<Cart> cartRemoveEntity = cartController.removeFromcart(modifyCartRequest);
        Assert.assertEquals(200, cartRemoveEntity.getStatusCodeValue());
    }

    private List<Item> createItemsList() {
        List<Item> list = new ArrayList<>();
        list.add(SareetaApplicationTests.getItem(1L, "Item 1", "Description 1", BigDecimal.TEN));
        list.add(SareetaApplicationTests.getItem(2L, "Item 2", "Description 2", BigDecimal.valueOf(20)));
        list.add(SareetaApplicationTests.getItem(3L, "Item 3", "Description 3", BigDecimal.valueOf(30)));
        return list;
    }
}
