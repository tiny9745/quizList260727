package com.example.quizList260727.quiz.dto.response;

import java.util.List;

/* 整張問卷的統計結果 */
public class QuizStatResponse {
	private Long quizId;
	private String quizTitle;
	private Long totalRespondents; // 總填答人數
	private List<QuestionStatDto> questionStats;

	public Long getQuizId() {
		return quizId;
	}

	public void setQuizId(Long quizId) {
		this.quizId = quizId;
	}

	public String getQuizTitle() {
		return quizTitle;
	}

	public void setQuizTitle(String quizTitle) {
		this.quizTitle = quizTitle;
	}

	public Long getTotalRespondents() {
		return totalRespondents;
	}

	public void setTotalRespondents(Long totalRespondents) {
		this.totalRespondents = totalRespondents;
	}

	public List<QuestionStatDto> getQuestionStats() {
		return questionStats;
	}

	public void setQuestionStats(List<QuestionStatDto> questionStats) {
		this.questionStats = questionStats;
	}
}
