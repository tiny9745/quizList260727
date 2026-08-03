package com.example.quizList260727.user.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MemberLevel {
	SUPER_ADMIN("超級管理者"), //
	ADMIN("一般管理者"), //
	USER("一般使用者"), //
	ANONYMOUS_USER("匿名使用者"), //
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
		case "A", "SUPER_ADMIN" -> SUPER_ADMIN;
		case "B", "ADMIN" -> ADMIN;
		case "C", "USER" -> USER;
		case "D", "ANONYMOUS_USER" -> ANONYMOUS_USER;
		case "E", "VISITOR" -> VISITOR;
		default -> throw new IllegalArgumentException("Invalid member level: " + input);
		};
	}
}
