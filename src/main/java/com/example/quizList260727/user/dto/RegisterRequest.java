package com.example.quizList260727.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegisterRequest {

	@NotBlank
	private String name;

	@NotBlank
	private String phone;

	@NotBlank
	private String password;

	@NotBlank
	private String email;

	@NotNull
	private Integer age;

	public String getName() {
		return name;
	}
	
	public RegisterRequest() {
		
	}

	public RegisterRequest(@NotBlank String name, @NotBlank String phone, @NotBlank String password,
			@NotBlank String email, @NotNull Integer age) {
		this.name = name;
		this.phone = phone;
		this.password = password;
		this.email = email;
		this.age = age;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
}
