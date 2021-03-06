package com.example.demo.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	private final UserRepository userRepository;
	private final CartRepository cartRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public UserController(UserRepository userRepository, CartRepository cartRepository) {
		this.userRepository = userRepository;
		this.cartRepository = cartRepository;
		this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
	}

	private static final Logger logger = LoggerFactory.getLogger("eCommerce");

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		try {
			logger.info("Begin creating User {}", createUserRequest.getUsername());

			String password = createUserRequest.getPassword();

			if (password == null) {
				logger.error("Password is empty");
				return ResponseEntity.badRequest().build();
			}

			if (password.length() < 8 || !password.equals(createUserRequest.getConfirmPassword())) {
				logger.error("Password can not be less than 8 character also confirm password must be the same");
				return ResponseEntity.badRequest().build();
			}

			User user = new User();
			user.setUsername(createUserRequest.getUsername());
			Cart cart = new Cart();
			cartRepository.save(cart);
			user.setCart(cart);
			user.setPassword(bCryptPasswordEncoder.encode(password));
			userRepository.save(user);

			logger.info("User successfully created");
			return ResponseEntity.ok(user);
		} catch (Exception exception) {
			logger.error("ERROR: " + exception.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}
	
}
