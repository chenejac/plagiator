package com.ftn.plagiator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.plagiator.model.ResultItem;
import com.ftn.plagiator.repository.ResultItemRepository;

@Service
public class ResultItemService {

	@Autowired
	ResultItemRepository resultItemRepository;
	
	public ResultItem findOne(Long id) {
		return resultItemRepository.findById(id).get();
	}

	public ResultItem save(ResultItem resultItem) {
		return resultItemRepository.save(resultItem);
	}
}
