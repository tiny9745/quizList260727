package com.example.quizList260727.reply.dto;

import java.util.List;

import com.example.quizList260727.constants.ValidationMessage;

import jakarta.validation.constraints.NotNull;

public class QuestionAnswerRequest {
	@NotNull(message = ValidationMessage.QUESTION_ID_REQUIRED)
	private Long questionId;
	
	private List<Long> optionIds; // 單選或多選題對應的選項 ID 列表
	
	private String answerText; // 簡答題內容

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public List<Long> getOptionIds() {
		return optionIds;
	}

	public void setOptionIds(List<Long> optionIds) {
		this.optionIds = optionIds;
	}

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}
}
