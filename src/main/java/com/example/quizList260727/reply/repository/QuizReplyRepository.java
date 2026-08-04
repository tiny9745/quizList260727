package com.example.quizList260727.reply.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.quizList260727.reply.entity.QuizReply;

@Repository
public interface QuizReplyRepository extends JpaRepository<QuizReply, Long> {
	@Modifying
	@Query(value = "INSERT INTO quiz_reply (quiz_id, user_email) " //
			+ " VALUES (:quizId, :userEmail)", nativeQuery = true)
	public void insertQuizReply(//
			@Param("quizId") Long quizId, //
			@Param("userEmail") String userEmail);

	@Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
	public Long getLastInsertedId();
}
