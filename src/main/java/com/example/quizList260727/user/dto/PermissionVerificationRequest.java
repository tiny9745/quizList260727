package com.example.quizList260727.user.dto;

import jakarta.validation.constraints.NotBlank;

public class PermissionVerificationRequest {
	
	@NotBlank
	private String email;
	
	public PermissionVerificationRequest() {
		
	}

	public PermissionVerificationRequest(@NotBlank String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
