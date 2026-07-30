package com.example.quizList260727.quiz.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.quizList260727.quiz.entity.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
	@Query(value = "SELECT * FROM quiz ORDER BY id DESC", nativeQuery = true)
	public List<Quiz> findAllQuizzes();

	@Query(value = "SELECT * FROM quiz WHERE id = ?1", nativeQuery = true)
	public Optional<Quiz> findQuizById(Long id);

	@Modifying
	@Query(value = "INSERT INTO quiz (title, description, start_date, end_date, is_published) "//
			+ " VALUES (:title, :description, :startDate, :endDate, :isPublished)", //
			nativeQuery = true)
	public void insertQuiz(//
			@Param("title") String title, //
			@Param("description") String description, //
			@Param("startDate") LocalDate startDate, //
			@Param("endDate") LocalDate endDate, //
			@Param("isPublished") Boolean isPublished);

	@Modifying
	@Query(value = "UPDATE quiz SET title = :title, description = :description, "//
			+ " start_date = :startDate, end_date = :endDate, is_published = :isPublished "//
			+ " WHERE id = :id", nativeQuery = true)
	public int updateQuiz(//
			@Param("id") Long id, //
			@Param("title") String title, //
			@Param("description") String description, //
			@Param("startDate") LocalDate startDate, //
			@Param("endDate") LocalDate endDate, //
			@Param("isPublished") Boolean isPublished);

	/**
	 * 只更新 is_published 這一個欄位，不觸碰 title/description/start_date/end_date， 供
	 * QuizService.updatePublishStatus() 呼叫，切換發布狀態時不會連帶影響問卷本身其他資訊。
	 */
	@Modifying
	@Query(value = "UPDATE quiz SET is_published = :isPublished WHERE id = :id", //
			nativeQuery = true)
	public int updatePublishStatus(//
			@Param("id") Long id, //
			@Param("isPublished") Boolean isPublished);

	@Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
	public Long getLastInsertedId();
}