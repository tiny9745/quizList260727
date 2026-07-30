package com.example.quizList260727.quiz.dto.request;

import com.example.quizList260727.constants.ValidationMessage;

import jakarta.validation.constraints.NotNull;

public class PublishRequest {
	@NotNull(message = ValidationMessage.QUIZ_ID_REQUIRED)
	private Long id;
	
	@NotNull(message = ValidationMessage.QUIZ_PUBLISHED_REQUIRED)
	private Boolean isPublished = false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getIsPublished() {
		return isPublished;
	}

	public void setIsPublished(Boolean isPublished) {
		this.isPublished = isPublished;
	}
	
}
