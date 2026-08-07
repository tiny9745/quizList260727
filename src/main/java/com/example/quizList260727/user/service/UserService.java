package com.example.quizList260727.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.quizList260727.security.JwtService;
import com.example.quizList260727.user.dto.LoginRequest;
import com.example.quizList260727.user.dto.LoginResponse;
import com.example.quizList260727.user.dto.PermissionVerificationRequest;
import com.example.quizList260727.user.dto.PermissionVerificationResponse;
import com.example.quizList260727.user.dto.RegisterRequest;
import com.example.quizList260727.user.entity.User;
import com.example.quizList260727.user.enums.MemberLevel;
import com.example.quizList260727.user.respository.UserRepository;

@Service
public class UserService {
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final JwtService jwtService;

	private String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
	private String passwordRegex = "^[A-Za-z][A-Za-z0-9]{7,15}$";

	public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.jwtService = jwtService;
	}

	@Transactional(readOnly = true)
	public LoginResponse logInByUser(LoginRequest Request) {

		if (!Request.getEmail().matches(emailRegex)) {
			throw new RuntimeException("Email 格式錯誤");
		}

		if (!Request.getPassword().matches(passwordRegex)) {
			throw new RuntimeException("Password 格式錯誤");
		}

		// 查詢 User的Email
		User user = userRepository.findByEmail(Request.getEmail()).orElseThrow(() -> new RuntimeException("帳號不存在"));

		if (!passwordEncoder.matches(Request.getPassword(), user.getPassword())) {
			throw new RuntimeException("密碼錯誤");
		}

		// 登入成功，簽發 Access Token，前端會存進 sessionStorage，
		// 之後呼叫需要登入的 API (例如 /api/quiz/**) 時帶在 Authorization Header
		String token = jwtService.generateToken(user.getEmail(), user.getPermissions().name());

		return new LoginResponse(user.getEmail(), user.getName(), user.getPermissions(), token);
	}

	@Transactional
	public void registerByUser(RegisterRequest registerRequest) {

	}

	@Transactional
	public PermissionVerificationResponse permissionVerification(PermissionVerificationRequest request) {
		PermissionVerificationResponse response = new PermissionVerificationResponse(MemberLevel.MEMBER);
		return response;
	}
	
	

}