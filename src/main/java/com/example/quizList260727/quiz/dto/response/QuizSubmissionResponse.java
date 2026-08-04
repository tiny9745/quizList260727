package com.example.quizList260727.quiz.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/* 單一填答者的完整提交紀錄 */
public class QuizSubmissionResponse {
	private Long responseId;
	private Long quizId;
	private String userEmail;
	private LocalDateTime submittedAt;
	private List<AnswerDetail> answers;
	public Long getResponseId() {
		return responseId;
	}
	public void setResponseId(Long responseId) {
		this.responseId = responseId;
	}
	public Long getQuizId() {
		return quizId;
	}
	public void setQuizId(Long quizId) {
		this.quizId = quizId;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public LocalDateTime getSubmittedAt() {
		return submittedAt;
	}
	public void setSubmittedAt(LocalDateTime submittedAt) {
		this.submittedAt = submittedAt;
	}
	public List<AnswerDetail> getAnswers() {
		return answers;
	}
	public void setAnswers(List<AnswerDetail> answers) {
		this.answers = answers;
	}
}

