package com.example.quizList260727.quiz.dto.request;

import java.util.List;

import com.example.quizList260727.constants.ValidationMessage;
import com.example.quizList260727.quiz.enums.QuestionType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class QuestionRequest {
	@NotNull(message = ValidationMessage.QUESTION_NUM_REQUIRED)
	private Integer questionNum; // 問題編號 (如: 1, 2, 3)

	@NotBlank(message = ValidationMessage.QUESTION_TITLE_REQUIRED)
	@Size(max = 200, message = ValidationMessage.QUESTION_TITLE_OVER_LIMIT)
	private String title;

	/**
	 * Spring Boot 預設使用 Jackson 解析 JSON。當前端傳送 "SINGLE"、"MULTI" 或 "TEXT" 等字串時，
	 * Jackson 會自動尋找 QuestionType 中名稱完全一致的列舉值並進行反序列化 <br>
	 * 注意: 原本是有區分大小寫，但在 QuestionType 中有寫了一個不區分大小寫自動反序列化的方法
	 */
	@NotNull(message = ValidationMessage.QUESTION_TYPE_REQUIRED)
	private QuestionType type;
	private Boolean isRequired = true;

	// 由於"問答(簡答)題"選項為null，屬於商業邏輯而非格式，因此不在此驗證
	@Valid
	private List<QuestionOptionRequest> options;

	public Integer getQuestionNum() {
		return questionNum;
	}

	public void setQuestionNum(Integer questionNum) {
		this.questionNum = questionNum;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public QuestionType getType() {
		return type;
	}

	public void setType(QuestionType type) {
		this.type = type;
	}

	public Boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

	public List<QuestionOptionRequest> getOptions() {
		return options;
	}

	public void setOptions(List<QuestionOptionRequest> options) {
		this.options = options;
	}
}
