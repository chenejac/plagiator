package com.ftn.plagiator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ftn.plagiator.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{

}
