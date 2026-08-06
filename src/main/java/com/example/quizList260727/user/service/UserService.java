package com.example.quizList260727.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.quizList260727.user.dto.LogInRequest;
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

	private String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
	private String passwordRegex = "^[A-Za-z][A-Za-z0-9]{7,15}$";

	public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public LoginResponse logInByUser(LogInRequest Request) {

		if (!Request.getEmail().matches(emailRegex)) {
			throw new RuntimeException("Email 格式錯誤");
		}

		if (!Request.getPassword().matches(passwordRegex)) {
			throw new RuntimeException("Password 格式錯誤");
		}

		// 查詢 User的Email
		User user = userRepository.findByEmail(Request.getEmail()).orElseThrow(() -> new RuntimeException("帳號不存在"));

		// 尚未使用雜湊
//		if (!Request.getPassword().equals(user.getPassword())) {
//			throw new RuntimeException("密碼錯誤");
//		}
		if (!passwordEncoder.matches(Request.getPassword(), user.getPassword())) {
		    throw new RuntimeException("密碼錯誤");
		}

		return new LoginResponse(user.getEmail(), user.getName(), user.getPermissions());
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
