package com.example.quizList260727.reply.dto;

import java.util.List;

import com.example.quizList260727.constants.ValidationMessage;

import jakarta.validation.constraints.NotNull;

public class QuestionAnswerRequest {
	@NotNull(message = ValidationMessage.QUESTION_ID_REQUIRED)
	private Long questionId;

	private List<String> optionCodes; // 使用者選擇的選項代碼（對應 option_code，例如 "A", "B"）

	private String answerText; // 簡答題內容

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public List<String> getOptionCodes() {
		return optionCodes;
	}

	public void setOptionCodes(List<String> optionCodes) {
		this.optionCodes = optionCodes;
	}

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}
}
