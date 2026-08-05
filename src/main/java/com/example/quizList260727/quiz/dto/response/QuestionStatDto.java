package com.example.quizList260727.quiz.dto.response;

import java.util.List;

import com.example.quizList260727.quiz.enums.QuestionType;

/* 單一問題統計結果 */
public class QuestionStatDto {
	private Long questionId;
	private Integer questionNum;
	private String questionTitle;
	private QuestionType questionType;
	// 選擇題統計結果 (單選 / 多選)
	private List<OptionStatDto> optionStats;
	// 簡答題所有填答文字清單
	private List<String> textAnswers;

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Integer getQuestionNum() {
		return questionNum;
	}

	public void setQuestionNum(Integer questionNum) {
		this.questionNum = questionNum;
	}

	public String getQuestionTitle() {
		return questionTitle;
	}

	public void setQuestionTitle(String questionTitle) {
		this.questionTitle = questionTitle;
	}

	public QuestionType getQuestionType() {
		return questionType;
	}

	public void setQuestionType(QuestionType questionType) {
		this.questionType = questionType;
	}

	public List<OptionStatDto> getOptionStats() {
		return optionStats;
	}

	public void setOptionStats(List<OptionStatDto> optionStats) {
		this.optionStats = optionStats;
	}

	public List<String> getTextAnswers() {
		return textAnswers;
	}

	public void setTextAnswers(List<String> textAnswers) {
		this.textAnswers = textAnswers;
	}
}
