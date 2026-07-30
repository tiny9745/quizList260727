package com.example.quizList260727.reply.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quizList260727.reply.entity.ResponseDetail;

@Repository
public interface ResponseDetailRepository extends JpaRepository<ResponseDetail, Long> {

}
