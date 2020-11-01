package com.example.demo.controllers;

import com.example.demo.SareetaApplication;
import com.example.demo.SareetaApplicationTests;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SareetaApplication.class)
public class OrderControllerTest {
    @Autowired
    private OrderController orderController;

    private final UserRepository userRepository = mock(UserRepository.class);;
    private final OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        SareetaApplicationTests.injectObjects(orderController, "userRepository", userRepository);
        SareetaApplicationTests.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submitOrder() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        Cart cart = SareetaApplicationTests.getCart();
        cart.setUser(user);
        user.setCart(cart);
        when(userRepository.findByUsername("username")).thenReturn(user);
        ResponseEntity<UserOrder> response = orderController.submit("username");
        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        UserOrder userOrder = response.getBody();
        Assert.assertNotNull(userOrder);
        Assert.assertEquals(cart.getItems(), userOrder.getItems());
        Assert.assertEquals(user, userOrder.getUser());
        Mockito.verify(userRepository, times(1)).findByUsername("username");
        Mockito.verify(orderRepository, times(1)).save(userOrder);
    }

    @Test
    public void submitOrderFailed() {
        when(userRepository.findByUsername("username")).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit("username");
        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        Mockito.verify(userRepository, times(1)).findByUsername("username");
        Mockito.verify(orderRepository , never()).save(Mockito.any());
    }

    @Test
    public void getOrders() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        Item item1 = SareetaApplicationTests.getItem(1L, "Item 1", "Item 1 Description", BigDecimal.TEN);
        UserOrder userOrder1 = new UserOrder();
        userOrder1.setUser(user);
        userOrder1.setItems(Arrays.asList(item1));
        userOrder1.setTotal(BigDecimal.TEN);
        UserOrder userOrder2 = new UserOrder();
        userOrder2.setUser(user);
        userOrder2.setItems(Arrays.asList(item1));
        userOrder2.setTotal(BigDecimal.TEN);
        when(userRepository.findByUsername("username")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(userOrder1, userOrder2));
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("username");
        Assert.assertNotNull(response);
        List<UserOrder> responseBody = response.getBody();
        Assert.assertEquals(Arrays.asList(userOrder1, userOrder2), responseBody);
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        Mockito.verify(userRepository, times(1)).findByUsername("username");
        Mockito.verify(orderRepository  , times(1)).findByUser(user);
    }

    @Test
    public void getHistoryFailed() {
        ResponseEntity<UserOrder> response = orderController.submit("invalidUser");
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
