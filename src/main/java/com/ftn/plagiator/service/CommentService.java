package com.ftn.plagiator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.plagiator.model.Comment;
import com.ftn.plagiator.repository.CommentRepository;

@Service
public class CommentService {
	
	@Autowired
	CommentRepository commentRepository;

	public Comment findOne(Long id) {
		return commentRepository.findById(id).get();
	}

	public Comment save(Comment comment) { 
		return commentRepository.save(comment);
	}

}
