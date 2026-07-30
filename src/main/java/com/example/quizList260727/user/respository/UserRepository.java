package com.example.quizList260727.user.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quizList260727.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User,String>{

}
