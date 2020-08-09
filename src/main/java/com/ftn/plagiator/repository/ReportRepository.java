package com.ftn.plagiator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ftn.plagiator.model.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>{
	
	public Report findByPaperUploadedIdAndPaperToCheckId(Long paperToUploadId, Long paperToCheckId);
	
	public List<Report> findByPaperUploadedIdOrPaperToCheckId(Long paperId, Long paperId2);
	
	public List<Report> findByPaperToCheckId(Long paperId);

}
