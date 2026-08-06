package com.example.quizList260727.user.dto;

import com.example.quizList260727.user.enums.MemberLevel;

public class PermissionVerificationResponse {
	
	private MemberLevel permissions;
	
	public PermissionVerificationResponse(MemberLevel permissions) {
		this.permissions = permissions;
	}

	public MemberLevel getPermissions() {
		return permissions;
	}

	public void setPermissions(MemberLevel permissions) {
		this.permissions = permissions;
	}
}
