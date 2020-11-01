package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SareetaApplicationTests {

	public static User getUser(String username, String password) {
		User user = new User();
		user.setId(1L);
		user.setUsername(username);
		user.setPassword(password);
		user.setCart(getCart());
		return user;
	}

	public static Cart getCart() {
		Cart cart = new Cart();
		cart.addItem(getItem(1L, "Item 1", "Description 1", BigDecimal.TEN));
		cart.addItem(getItem(2L, "Item 2", "Description 2", BigDecimal.valueOf(20)));
		cart.addItem(getItem(3L, "Item 3", "Description 3", BigDecimal.valueOf(30)));
		return cart;
	}

	public static Item getItem(Long id, String name, String description, BigDecimal price) {
		Item item = new Item();
		item.setId(id);
		item.setName(name);
		item.setDescription(description);
		item.setPrice(price);
		return item;
	}

	public static void injectObjects(Object target, String fieldName, Object toInject){
		try{
			boolean wasPrivate = false;
			Field f = target.getClass().getDeclaredField(fieldName);
			if(!f.isAccessible()) {
				f.setAccessible(true);
				wasPrivate = true;
			}
			f.set(target, toInject);
			if(wasPrivate) f.setAccessible(false);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
