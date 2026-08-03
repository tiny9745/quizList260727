package com.example.quizList260727.reply.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.quizList260727.reply.entity.ResponseDetail;

@Repository
public interface ResponseDetailRepository extends JpaRepository<ResponseDetail, Long> {
	@Modifying
	@Query(value = "INSERT INTO response_detail (response_id, question_id, option_id, answer_text) "//
			+ " VALUES (:responseId, :questionId, :optionId, :answerText)", nativeQuery = true)
	public void insertDetail( //
			@Param("responseId") Long responseId, //
			@Param("questionId") Long questionId, //
			@Param("optionId") Long optionId, //
			@Param("answerText") String answerText);
}
