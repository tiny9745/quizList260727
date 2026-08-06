package com.example.quizList260727.user.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MemberLevel {
    SYSTEM_ADMIN("系統管理員"),
    MANAGER("管理者"),
    MEMBER("一般會員"),
    GUEST("匿名使用者"),
    VISITOR("訪客");

	private String permissions;

	private MemberLevel(String permissions) {
		this.permissions = permissions;
	}

	public String getPermissions() {
		return permissions;
	}

	@JsonCreator
	public static MemberLevel memberLevelStringFromEnum(String input) {

		if (input == null || input.isBlank()) {
			return null;
		}

		return switch (input.toUpperCase()) {
		case "A", "SYSTEM_ADMIN" -> SYSTEM_ADMIN;
		case "B", "MANAGER" -> MANAGER;
		case "C", "MEMBER" -> MEMBER;
		case "D", "GUEST" -> GUEST;
		case "E", "VISITOR" -> VISITOR;
		default -> throw new IllegalArgumentException("Invalid member level: " + input);
		};
	}
}
