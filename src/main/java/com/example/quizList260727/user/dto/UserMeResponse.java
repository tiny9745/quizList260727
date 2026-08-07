package com.example.quizList260727.user.dto;

import com.example.quizList260727.user.enums.MemberLevel;

public class UserMeResponse {

    private String email;
    private String userName;
    private MemberLevel memberLevel;

    public UserMeResponse(String email, String userName, MemberLevel memberLevel) {
        this.email = email;
        this.userName = userName;
        this.memberLevel = memberLevel;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public MemberLevel getMemberLevel() {
        return memberLevel;
    }
}