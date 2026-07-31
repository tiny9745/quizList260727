package com.example.quizList260727.quiz.dto.request;

import java.util.List;

import com.example.quizList260727.constants.ValidationMessage;

import jakarta.validation.constraints.NotEmpty;

public class QuizDeleteRequest {
	
	@NotEmpty(message = ValidationMessage.QUIZ_IDS_REQUIRED)
	   private List<Long> quizIds;
	   public List<Long> getQuizIds() {
	       return quizIds;
	   }
	   public void setQuizIds(List<Long> quizIds) {
	       this.quizIds = quizIds;
	   }

}
