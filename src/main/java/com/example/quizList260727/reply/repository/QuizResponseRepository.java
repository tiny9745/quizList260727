package com.example.quizList260727.reply.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quizList260727.reply.entity.QuizReply;

@Repository
public interface QuizResponseRepository extends JpaRepository<QuizReply, Long> {

}
