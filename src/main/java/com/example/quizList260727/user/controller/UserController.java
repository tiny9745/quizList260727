package com.example.quizList260727.user.controller;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quizList260727.user.dto.LoginRequest;
import com.example.quizList260727.user.dto.LoginResponse;
import com.example.quizList260727.user.dto.UserMeResponse;
import com.example.quizList260727.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

	UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/log-in")
	public ResponseEntity<LoginResponse> logInByPassword(@Valid @RequestBody LoginRequest request) {
		LoginResponse result = userService.logInByUser(request);
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/me")
	public ResponseEntity<UserMeResponse> getCurrentUser(Authentication authentication) {

	    String email = authentication.getName();

	    UserMeResponse user = userService.getUserByEmail(email);

	    return ResponseEntity.ok(user);
	}
}
