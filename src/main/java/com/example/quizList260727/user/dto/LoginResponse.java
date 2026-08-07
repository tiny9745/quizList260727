package com.example.quizList260727.user.dto;

import com.example.quizList260727.user.enums.MemberLevel;

public class LoginResponse {
	private String email;

	private String name;

	private MemberLevel permissions;

	private String token;

	public LoginResponse() {

	}

	public LoginResponse(String email, String name, MemberLevel permissions, String token) {
		this.email = email;
		this.name = name;
		this.permissions = permissions;
		this.token = token;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MemberLevel getPermissions() {
		return permissions;
	}

	public void setPermissions(MemberLevel permissions) {
		this.permissions = permissions;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}