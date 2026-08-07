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
import com.example.quizList260727.user.dto.UserMeResponse;
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
	private String phoneRegex = "^(09\\d{8}|0[2-8]\\d{7,8})$";

	private String emailFromatError = "Email 格式錯誤";
	private String passwordFromatError = "Password 格式錯誤";
	private String userNotExist = "帳號不存在";

	public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.jwtService = jwtService;
	}

	@Transactional(readOnly = true)
	public LoginResponse logInByUser(LoginRequest request) {

		if (!request.getEmail().matches(emailRegex)) {
			throw new RuntimeException(emailFromatError);
		}

		if (!request.getPassword().matches(passwordRegex)) {
			throw new RuntimeException(passwordFromatError);
		}

		// 查詢 User的Email
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException(userNotExist));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new RuntimeException("密碼錯誤");
		}

		// 登入成功，簽發 Access Token，前端會存進 sessionStorage，
		// 之後呼叫需要登入的 API (例如 /api/quiz/**) 時帶在 Authorization Header
		String token = jwtService.generateToken(user.getEmail(), user.getPermissions().name());

		return new LoginResponse(user.getEmail(), user.getName(), user.getPermissions(), token);
	}

	@Transactional
	public void registerByUser(RegisterRequest request) {
		if (!request.getEmail().matches(emailRegex)) {
			throw new RuntimeException(emailFromatError);
		}

		if (!request.getPassword().matches(passwordRegex)) {
			throw new RuntimeException(passwordFromatError);
		}

		if (!request.getPhone().matches(phoneRegex)) {
			throw new RuntimeException("電話格式異常");
		}

		if (request.getAge() < 0 || request.getAge() > 200) {
			throw new RuntimeException("年齡異常");
		}

		if (request.getName().length() > 50) {
			throw new RuntimeException("名字長度過長");
		}

		if (userRepository.findByEmail(request.getEmail()) != null) {
			throw new RuntimeException("User is exist");
		}

	}

	@Transactional
	public PermissionVerificationResponse permissionVerification(PermissionVerificationRequest request) {
		PermissionVerificationResponse response = new PermissionVerificationResponse(MemberLevel.MEMBER);
		return response;
	}

	public UserMeResponse getUserByEmail(String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException(userNotExist));
		return new UserMeResponse(user.getEmail(), user.getName(), user.getPermissions());
	}

}