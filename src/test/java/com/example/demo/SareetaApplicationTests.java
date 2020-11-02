package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SareetaApplicationTests {

	private UserController userController;
	private CartController cartController;
	private OrderController orderController;
	private final UserRepository userRepository = mock(UserRepository.class);
	private final CartRepository cartRepository = mock(CartRepository.class);
	private final ItemRepository itemRepository = mock(ItemRepository.class);
	private final OrderRepository orderRepository = mock(OrderRepository.class);
	private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

	@Before
	public void settings() {
		userController = new UserController(userRepository, cartRepository);
		TestUtils.injectObjects(userController, "userRepository", userRepository);
		TestUtils.injectObjects(userController, "cartRepository", cartRepository);
		TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
		cartController = new CartController();
		TestUtils.injectObjects(cartController, "userRepository", userRepository);
		TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
		TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
		orderController = new OrderController();
		TestUtils.injectObjects(orderController, "userRepository", userRepository);
		TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
	}

	@Test
	public void createUser() {
		when(encoder.encode(TestUtils.TEST_PASSWORD)).thenReturn(TestUtils.TEST_HASHED_TEXT);
		ResponseEntity<User> responseEntity = TestUtils.createUserEntity(userController);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		assertNotNull(responseEntity);
		User user = responseEntity.getBody();
		assertNotNull(user);

		assertEquals(0, user.getId());
		assertEquals(TestUtils.TEST_USERNAME, user.getUsername());
		assertEquals(TestUtils.TEST_HASHED_TEXT, user.getPassword());
	}

	@Test
	public void findUserByUsername() {
		when(encoder.encode(TestUtils.TEST_PASSWORD)).thenReturn(TestUtils.TEST_HASHED_TEXT);
		ResponseEntity<User> responseEntity = TestUtils.createUserEntity(userController);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		assertNotNull(responseEntity);
		User user = responseEntity.getBody();
		assertNotNull(user);
		assertEquals(0, user.getId());
		assertEquals(TestUtils.TEST_USERNAME, user.getUsername());
		assertEquals(TestUtils.TEST_HASHED_TEXT, user.getPassword());
		when(userRepository.findByUsername(TestUtils.TEST_USERNAME)).thenReturn(user);

		ResponseEntity<User> foundUser = userController.findByUserName(user.getUsername());
		assertNotNull(foundUser);
		assertEquals(HttpStatus.OK, foundUser.getStatusCode());
		User foundUserEntity = foundUser.getBody();
		assertNotNull(foundUserEntity);
		assertEquals(user.getUsername(), foundUserEntity.getUsername());
	}

	@Test
	public void findUserById() {
		when(encoder.encode(TestUtils.TEST_PASSWORD)).thenReturn(TestUtils.TEST_HASHED_TEXT);
		ResponseEntity<User> responseEntity = TestUtils.createUserEntity(userController);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		assertNotNull(responseEntity);
		User user = responseEntity.getBody();
		assertNotNull(user);
		assertEquals(0, user.getId());
		assertEquals(TestUtils.TEST_USERNAME, user.getUsername());
		assertEquals(TestUtils.TEST_HASHED_TEXT, user.getPassword());
		when(userRepository.findByUsername(TestUtils.TEST_USERNAME)).thenReturn(user);

		ResponseEntity<User> foundUser = userController.findByUserName(user.getUsername());
		assertNotNull(foundUser);
		assertEquals(HttpStatus.OK, foundUser.getStatusCode());
		User foundUserEntity = foundUser.getBody();
		assertNotNull(foundUserEntity);
		assertEquals(user.getId(), foundUserEntity.getId());
	}

	@Test
	public void createUserFailed() {
		CreateUserRequest fakeRequest = new CreateUserRequest();
		fakeRequest.setUsername(TestUtils.TEST_USERNAME);
		fakeRequest.setPassword(TestUtils.TEST_PASSWORD);
		fakeRequest.setConfirmPassword("AnotherPassword");

		ResponseEntity<User> responseEntity = userController.createUser(fakeRequest);
		assertNotNull(responseEntity);
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}

	@Test
	public void addItemToCart() {
		User user = TestUtils.getuserEntity();
		Item item = TestUtils.getItemEntity();
		Cart cart = TestUtils.getCartEntity(user, item);
		user.setCart(cart);
		when(userRepository.findByUsername(TestUtils.TEST_USERNAME)).thenReturn(user);
		when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

		ResponseEntity<Cart> responseEntity = TestUtils.getItem(cartController);
		assertNotNull(responseEntity);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		Cart cartResponse = responseEntity.getBody();
		assertNotNull(cartResponse);
		assertEquals(cartResponse.getItems().size(), 1);
		assertEquals(cartResponse.getItems().get(0).getName(), item.getName());
	}

	@Test
	public void orderList() {
		User user = TestUtils.getuserEntity();
		Item item = TestUtils.getItemEntity();
		Cart cart = TestUtils.getCartEntity(user, item);
		cart.addItem(item);
		user.setCart(cart);

		UserOrder fakeUserOrder = UserOrder.createFromCart(cart);
		when(userRepository.findByUsername(TestUtils.TEST_USERNAME)).thenReturn(user);
		when(orderRepository.findByUser(user)).thenReturn(Collections.singletonList(fakeUserOrder));
		ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser(TestUtils.TEST_USERNAME);
		assertNotNull(responseEntity);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		List<UserOrder> userOrders = responseEntity.getBody();
		assertNotNull(userOrders);
		assertEquals(userOrders.size(), 1);
		assertEquals(userOrders.get(0).getItems().get(0).getName(), item.getName());
	}

	@Test
	public void orderSubmit(){
		User user = TestUtils.getuserEntity();
		Item item = TestUtils.getItemEntity();
		Cart cart = TestUtils.getCartEntity(user, item);
		cart.addItem(item);
		user.setCart(cart);

		when(userRepository.findByUsername(TestUtils.TEST_USERNAME)).thenReturn(user);
		ResponseEntity<UserOrder> userOrderResponseEntity = orderController.submit(TestUtils.TEST_USERNAME);
		assertNotNull(userOrderResponseEntity);
		assertEquals(HttpStatus.OK, userOrderResponseEntity.getStatusCode());

		UserOrder userOrder = userOrderResponseEntity.getBody();
		assertNotNull(userOrder);

		assertEquals(userOrder.getTotal(), cart.getTotal());
	}

	@Test
	public void removeItemFromCard() {
		User user = TestUtils.getuserEntity();
		Item item = TestUtils.getItemEntity();
		Cart cart = TestUtils.getCartEntity(user, item);
		user.setCart(cart);
		when(userRepository.findByUsername(TestUtils.TEST_USERNAME)).thenReturn(user);
		when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

		ResponseEntity<Cart> responseEntity = TestUtils.getItem(cartController);
		assertNotNull(responseEntity);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		Cart cartResponse = responseEntity.getBody();
		assertNotNull(cartResponse);
		assertEquals(cartResponse.getItems().size(), 1);
		assertEquals(cartResponse.getItems().get(0).getName(), item.getName());

		ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
		modifyCartRequest.setItemId(1);
		modifyCartRequest.setQuantity(1);
		modifyCartRequest.setUsername(TestUtils.TEST_USERNAME);
		ResponseEntity<Cart> responseRemoveEntity = cartController.removeFromcart(modifyCartRequest);
		assertNotNull(responseRemoveEntity);
		assertEquals(HttpStatus.OK, responseRemoveEntity.getStatusCode());
	}
}