package com.ftn.plagiator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.plagiator.elasticsearch.model.PaperElastic;
import com.ftn.plagiator.elasticsearch.repository.PaperElasticRepository;
import com.ftn.plagiator.model.Paper;
import com.ftn.plagiator.model.Report;
import com.ftn.plagiator.repository.PaperRepository;
import com.ftn.plagiator.repository.ReportRepository;

@Service
public class PaperService {

	@Autowired
	PaperRepository paperRepository;
	
	@Autowired
	PaperElasticRepository paperElasticRepository;
	
	@Autowired
	ReportRepository reportRepository;

	public Paper findOne(Long id) {
		return paperRepository.findById(id).get();
	}

	public Paper save(Paper paper) {
		Paper savedPaper = paperRepository.save(paper);
		return savedPaper;
	}
	
	public List<Paper> findAllFromElastic() {
		Iterable<PaperElastic> papersElastic = paperElasticRepository.findAll();
		List<Paper> retVal = new ArrayList<Paper>();
		
		papersElastic.forEach(item -> {
			Optional<Paper> optionalPaper = paperRepository.findById(item.getId());
			
			if(optionalPaper.isPresent()) {
				retVal.add(optionalPaper.get());
			}
		});
		
		return retVal;
	}
	
	public List<Paper> findAll() {
		return  paperRepository.findAll();
	}
	
	public void deleteById(Long id) {
		
		List<Report> reports = reportRepository.findByPaperUploadedIdOrPaperToCheckId(id, id);
		for(Report report: reports) {
			report.setPaperToCheck(null);
			report.setPaperUploaded(null);
			reportRepository.save(report);
		}
		
		paperRepository.deleteById(id);
	}

}
