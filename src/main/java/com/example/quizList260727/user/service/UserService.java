package com.example.quizList260727.user.service;

import java.time.LocalDateTime;

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
			throw new IllegalArgumentException(emailFromatError);
		}

		if (!request.getPassword().matches(passwordRegex)) {
			throw new IllegalArgumentException(passwordFromatError);
		}

		// 查詢 User的Email
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new IllegalArgumentException(userNotExist));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new IllegalArgumentException("密碼錯誤");
		}

		// 登入成功，簽發 Access Token，前端會存進 sessionStorage，
		// 之後呼叫需要登入的 API (例如 /api/quiz/**) 時帶在 Authorization Header
		String token = jwtService.generateToken(user.getEmail(), user.getPermissions().name());

		return new LoginResponse(user.getEmail(), user.getName(), user.getPermissions(), token);
	}

	@Transactional
	public void registerByUser(RegisterRequest request) {
		if (!request.getEmail().matches(emailRegex)) {
			throw new IllegalArgumentException(emailFromatError);
		}

		if (!request.getPassword().matches(passwordRegex)) {
			throw new IllegalArgumentException(passwordFromatError);
		}

		if (!request.getPhone().matches(phoneRegex)) {
			throw new IllegalArgumentException("電話格式異常");
		}

		if (request.getAge() < 0 || request.getAge() > 200) {
			throw new IllegalArgumentException("年齡異常");
		}

		if (request.getName().length() > 50) {
			throw new IllegalArgumentException("名字長度過長");
		}
		
		 // 1. 檢查 Email 是否已存在
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
		    throw new IllegalArgumentException("User is exist");
		}
		
		// 2. 建立 User Entity
	    User user = new User();

	    user.setName(request.getName());
	    user.setEmail(request.getEmail());
	    user.setPhone(request.getPhone());
	    user.setAge(request.getAge());
	    
	    user.setCreatedAt(LocalDateTime.now());
	    user.setPermissions(MemberLevel.MEMBER);
	    
	    // 3. 密碼不可直接存明文
	    user.setPassword(passwordEncoder.encode(request.getPassword()));

	    // 4. 儲存到資料庫
	    userRepository.save(user);

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