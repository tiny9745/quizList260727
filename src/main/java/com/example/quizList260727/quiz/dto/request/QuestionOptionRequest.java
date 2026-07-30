package com.example.quizList260727.quiz.dto.request;

import com.example.quizList260727.constants.ValidationMessage;
import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class QuestionOptionRequest {

	@JsonAlias({ "id" }) // 用來匹配前端的變數名稱
	@NotBlank(message = ValidationMessage.OPTION_CODE_REQUIRED)
	@Size(max = 10, message = ValidationMessage.OPTION_CODE_OVER_LIMIT)
	private String optionCode; // 選項編號 (如: A, B, 1, 2)

	@JsonAlias({ "option" })
	@NotBlank(message = ValidationMessage.OPTION_TEXT_REQUIRED)
	@Size(max = 200, message = ValidationMessage.OPTION_TEXT_OVER_LIMIT)
	private String optionText; // 選項內容

	public String getOptionCode() {
		return optionCode;
	}

	public void setOptionCode(String optionCode) {
		this.optionCode = optionCode;
	}

	public String getOptionText() {
		return optionText;
	}

	public void setOptionText(String optionText) {
		this.optionText = optionText;
	}
}
