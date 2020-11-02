package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SareetaApplication.class)
public class SareetaApplicationTests {

	@Autowired
	private CartController cartController;

	@Autowired
	private UserController userController;

	@Autowired
	private OrderController orderController;

	private final ItemRepository itemRepository = mock(ItemRepository.class);
	private final UserRepository userRepository = mock(UserRepository.class);;
	private final OrderRepository orderRepository = mock(OrderRepository.class);
	private final CartRepository cartRepository = mock(CartRepository.class);
	private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

	@Before
	public void prepare() {
		injectObjects(userController, "userRepository", userRepository);
		injectObjects(userController, "cartRepository", cartRepository);
		injectObjects(cartController, "userRepository", userRepository);
		injectObjects(cartController, "cartRepository", cartRepository);
		injectObjects(cartController, "itemRepository", itemRepository);
		injectObjects(orderController, "userRepository", userRepository);
		injectObjects(orderController, "orderRepository", orderRepository);
	}

	@Test
	public void createUser() {
//		when(encoder.encode("testPassword")).thenReturn("hashedText");
//		ResponseEntity<User> responseEntity = getUser("user", "testPassword");
//		Assert.assertNotNull(responseEntity);
//		User user = responseEntity.getBody();
//		Assert.assertEquals(0, user.getId());
//		Assert.assertEquals("user", user.getUsername());
//		Assert.assertEquals("hashedText", user.getPassword());
	}

	@Test
	public void createUserBadRequest() {
		CreateUserRequest fakeRequest = new CreateUserRequest();
		fakeRequest.setUsername("testUser");
		fakeRequest.setPassword("password");
		fakeRequest.setConfirmPassword("MISMATCH");
		ResponseEntity<User> responseEntity = userController.createUser(fakeRequest);
		Assert.assertNotNull(responseEntity);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}

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
		list.add(getItem(1L, "Item 1", "Description 1", BigDecimal.TEN));
		list.add(getItem(2L, "Item 2", "Description 2", BigDecimal.valueOf(20)));
		list.add(getItem(3L, "Item 3", "Description 3", BigDecimal.valueOf(30)));
		return list;
	}

	public static User getUser(String username, String password) {
		User user = new User();
		user.setId(1L);
		user.setUsername("testUser");
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
