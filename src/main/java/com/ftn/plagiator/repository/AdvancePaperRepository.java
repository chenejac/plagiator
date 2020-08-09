package com.ftn.plagiator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ftn.plagiator.model.AdvancePaper;

@Repository
public interface AdvancePaperRepository extends JpaRepository<AdvancePaper, Long>{

	List<AdvancePaper> findByResultItemId(Long id);
	
	List<AdvancePaper> findByPaperId(Long id);
}
