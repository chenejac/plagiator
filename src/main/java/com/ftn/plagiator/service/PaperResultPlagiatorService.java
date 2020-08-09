package com.ftn.plagiator.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.plagiator.model.PaperResultPlagiator;
import com.ftn.plagiator.repository.PaperResultPlagiatorRepository;

@Service
public class PaperResultPlagiatorService {
	
	@Autowired
	PaperResultPlagiatorRepository paperResultPlagiatorRepository;
	
	public PaperResultPlagiator findOne(Long id) {
		Optional<PaperResultPlagiator> optional = paperResultPlagiatorRepository.findById(id);
		
		if(optional.isPresent()) {
			return optional.get();
		}
		
		return null;
	}

	public PaperResultPlagiator save(PaperResultPlagiator paperResultPlagiator) {
		return paperResultPlagiatorRepository.save(paperResultPlagiator);
	}
	
	public PaperResultPlagiator findByUploadedPaperId(Long id) {
		return paperResultPlagiatorRepository.findByUploadedPaperId(id);
	}
}
