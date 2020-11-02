package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class TestUtils {

    public static final String TEST_USERNAME = "testingUser";
    public static final String TEST_PASSWORD = "password";
    public static final String TEST_HASHED_TEXT = "hashed";

    public static void injectObjects(Object target, String fieldName, Object toInject) {
        boolean wasPrivate = false;

        try {
            Field f = target.getClass().getDeclaredField(fieldName);

            if (!f.isAccessible()) {
                f.setAccessible(true);
                wasPrivate = true;
            }
            f.set(target, toInject);
            if (wasPrivate) f.setAccessible(false);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public static ResponseEntity<Cart> getItem(CartController cartController) {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername(TEST_USERNAME);
        return cartController.addTocart(modifyCartRequest);
    }

    public static ResponseEntity<User> createUserEntity(UserController userController) {
        CreateUserRequest fakeRequest = new CreateUserRequest();
        fakeRequest.setUsername(TEST_USERNAME);
        fakeRequest.setPassword(TEST_PASSWORD);
        fakeRequest.setConfirmPassword(TEST_PASSWORD);
        return userController.createUser(fakeRequest);
    }

    public static User getuserEntity() {
        User user = new User();
        user.setId(1);
        user.setUsername(TEST_USERNAME);
        user.setPassword(TEST_HASHED_TEXT);
        return user;
    }

    public static Item getItemEntity() {
        Item item = new Item();
        item.setId(1L);
        item.setDescription("Tested item");
        item.setName("My name is Item");
        item.setPrice(BigDecimal.TEN);
        return item;
    }

    public static Cart getCartEntity(User user, Item item) {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        return cart;
    }

}