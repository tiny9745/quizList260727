package com.example.quizList260727.quiz.dto.request;

import com.example.quizList260727.constants.ValidationMessage;

import jakarta.validation.constraints.NotNull;

public class PublishRequest {
	@NotNull(message = ValidationMessage.QUIZ_PUBLISHED_REQUIRED)
	private Boolean isPublished = false;

	public Boolean getIsPublished() {
		return isPublished;
	}

	public void setIsPublished(Boolean isPublished) {
		this.isPublished = isPublished;
	}
	
}
