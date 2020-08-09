package com.ftn.plagiator.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.plagiator.model.Report;
import com.ftn.plagiator.repository.ReportRepository;

@Service
public class ReportService {
	
	@Autowired
	ReportRepository reportRepository;
	
	public Report findOne(Long id) {
		return reportRepository.findById(id).get();
	}

	public Report save(Report report) {
		return reportRepository.save(report);
	}
	
	public Report findByPaperUploadedIdAndPaperToCheckId(Long paperToUploadId, Long paperToCheckId) {
		return reportRepository.findByPaperUploadedIdAndPaperToCheckId(paperToUploadId, paperToCheckId);
	}
	
	public List<Report> getReportsForDocument(Long paperId){
		return reportRepository.findByPaperToCheckId(paperId);
	}

}
