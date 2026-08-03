package com.example.quizList260727.reply.dto;

import java.util.List;

import com.example.quizList260727.constants.ValidationMessage;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class FillQuizRequest {
	@NotNull(message = ValidationMessage.QUIZ_ID_REQUIRED)
   private Long quizId;
   @NotBlank(message = ValidationMessage.EMAIL_REQUIRED)
   @Email(message = ValidationMessage.EMAIL_FORMAT_INVALID)
   private String userEmail;
   @Valid
   @NotEmpty(message = ValidationMessage.QUESTION_ANSWERS_REQUIRED)
   private List<QuestionAnswerRequest> answers;
   // Getters and Setters
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
   public List<QuestionAnswerRequest> getAnswers() {
       return answers;
   }
   public void setAnswers(List<QuestionAnswerRequest> answers) {
       this.answers = answers;
   }
}

