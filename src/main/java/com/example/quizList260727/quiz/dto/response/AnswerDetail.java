package com.example.quizList260727.quiz.dto.response;

import java.util.List;

import com.example.quizList260727.quiz.enums.QuestionType;

/* 單一問題的作答結果 */
public class AnswerDetail {
	// 題目 ID
	private Long questionId;
	// 問卷內的題目編號
	private Integer questionNum;
	// 題目名稱或內容
	private String questionTitle;
	private QuestionType questionType;
	// 選擇題: 列出所有選項及該填答者是否有勾選
	private List<OptionDetail> options;
	// 簡答題: 填答者的文字回覆
	private String answerText;

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

	public List<OptionDetail> getOptions() {
		return options;
	}

	public void setOptions(List<OptionDetail> options) {
		this.options = options;
	}

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}
}
