package com.ftn.plagiator.controller;

import java.io.File;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ftn.plagiator.dto.CommentDTO;
import com.ftn.plagiator.dto.PaperDTO;
import com.ftn.plagiator.dto.ReportDTO;
import com.ftn.plagiator.elasticsearch.repository.PaperElasticRepository;
import com.ftn.plagiator.model.AdvancePaper;
import com.ftn.plagiator.model.Comment;
import com.ftn.plagiator.model.Paper;
import com.ftn.plagiator.model.PaperResultPlagiator;
import com.ftn.plagiator.model.Report;
import com.ftn.plagiator.model.User;
import com.ftn.plagiator.service.AdvancePaperService;
import com.ftn.plagiator.service.CommentService;
import com.ftn.plagiator.service.PaperResultPlagiatorService;
import com.ftn.plagiator.service.PaperService;
import com.ftn.plagiator.service.ReportService;
import com.ftn.plagiator.service.UserService;
import com.ftn.plagiator.util.ObjectMapperUtil;
import com.ftn.plagiator.util.RoleConstants;

@RestController
@RequestMapping(value = "/api/papers")
public class PaperController {
	
	@Autowired
	PaperService paperService;
	
	@Autowired
	ObjectMapperUtil objectMapper;
	
	@Autowired
	CommentService commentService;
	
	@Autowired
	ReportService reportService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	PaperElasticRepository paperElasticRepository;
	
	@Autowired
	PaperResultPlagiatorService paperResultPlagiatorService;
	
	@Autowired
	AdvancePaperService advancePaperService;
	
	@GetMapping(produces = "application/json")
	public ResponseEntity<List<PaperDTO>> getPapers(Pageable page, HttpServletRequest request) {
		
		List<Paper> papers = paperService.findAll();
		List<PaperDTO> papersDTO = new ArrayList<PaperDTO>();
		
		Principal principal = request.getUserPrincipal();
        String email = principal.getName();
        User logged = userService.findByEmail(email);
		
		for(Paper paper: papers) {
			PaperDTO paperDTO = objectMapper.map(paper, PaperDTO.class);
			PaperResultPlagiator plagiator = paperResultPlagiatorService.findByUploadedPaperId(paper.getId());

			if(plagiator != null) {
				//nije od tog korisnika pa ne moze da ga vidi i korisnikova uloga nije admin
		    	if(!logged.getRole().getUserType().equals(RoleConstants.ROLE_ADMIN) //ako je admin nek prodje
		    			&& !plagiator.getUploadedPaper().getUser().getEmail().equals(email)) { //nisi ga ti uplodovao
		    		paperDTO.setPlagiatorId(null);
		    	}
		    	else { //inace moze da mu pristupi da vidi pregled. To znaci da postoji i da ima privilegiju.
		    		paperDTO.setPlagiatorId(plagiator.getId());
		    	}
			}
			else {
				paperDTO.setPlagiatorId(null);
			}
			
			
			
			papersDTO.add(paperDTO);
		}

		
		return new ResponseEntity<>(papersDTO, HttpStatus.OK);
	}
	
	@GetMapping(produces = "application/json", path = "/{id}")
	public ResponseEntity<PaperDTO> getPaperById(@PathVariable Long id) {
		
		return new ResponseEntity<>(objectMapper.map(paperService.findOne(id), 
				PaperDTO.class), HttpStatus.OK);
	}
	
	@GetMapping(produces = "application/json", path = "/{id}/comments")
	public ResponseEntity<List<CommentDTO>> getCommentsForPaper(@PathVariable Long id) {
		
		List<Report> reports = reportService.getReportsForDocument(id);
		List<CommentDTO> commentsDTO = new ArrayList<CommentDTO>();
		for(Report report: reports) {
			for(Comment oneComment: report.getComments()) {
				CommentDTO commentDTO = objectMapper.map(oneComment, CommentDTO.class);
				commentDTO.setPlagiatWith(report.getPaperUploaded().getTitle());
				commentsDTO.add(commentDTO);
			}
		}
		
		return new ResponseEntity<>(commentsDTO, HttpStatus.OK);
	}
	
	@PostMapping(produces = "application/json", path = "/comments")
	public ResponseEntity<ReportDTO> addComment(@RequestBody ReportDTO reportDTO, HttpServletRequest request) {
		
		Principal principal = request.getUserPrincipal();
        String email = principal.getName();
        User logged = userService.findByEmail(email);
        
        Report report = reportService.findByPaperUploadedIdAndPaperToCheckId(reportDTO.getPaperUploaded().getId(),
        		reportDTO.getPaperToCheck().getId());
        
        if(report == null) {
        	 report = new Report();
        }
		
		report.setPaperToCheck(reportDTO.getPaperToCheck());
		report.setPaperUploaded(reportDTO.getPaperUploaded());
		report = reportService.save(report);
		
		CommentDTO commentDTO = reportDTO.getComments().get(0);
		
		Comment comment = new Comment();
		comment.setReport(report);
		comment.setDatetime(Instant.now());
		comment.setMatcheNumber(commentDTO.getMatcheNumber());
		comment.setText(commentDTO.getText());
		comment.setUser(logged);
		comment = commentService.save(comment);
		
		return new ResponseEntity<>(objectMapper.map(report, ReportDTO.class), HttpStatus.OK);
	}
	
	
	@DeleteMapping(produces = "application/json", path = "/{id}")
	public ResponseEntity<Void> deletePaper(@PathVariable Long id) {	
		Paper paper = paperService.findOne(id);
		
		List<AdvancePaper> advances = advancePaperService.findByPaperId(id);
		for(AdvancePaper advancePaper: advances) {
			advancePaperService.delete(advancePaper.getId());
		}
		
		PaperResultPlagiator plagiator = paperResultPlagiatorService.findByUploadedPaperId(id);
		
		if(plagiator != null) {
			plagiator.setUploadedPaper(null);
			paperResultPlagiatorService.save(plagiator);
		}
		
		//obrisi fajl sistem
		File file = new File(paper.getPathForPDF()); 
        if(!file.delete()) 
        { 
        	System.out.println("Failed to delete the file");
        	return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        //obrisi index
        paperElasticRepository.deleteById(paper.getId());
        
		//obrisi bazu
        paperService.deleteById(id);
        
        
		//proveriti referencijalni integritet u report tabeli
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping(produces = "application/json", path = "/{id}/plagiator")
	public ResponseEntity<Long> getPaperPlagiator(@PathVariable Long id) {	
		PaperResultPlagiator plagiator = paperResultPlagiatorService.findByUploadedPaperId(id);

		return new ResponseEntity<>(plagiator.getId(), HttpStatus.OK);
	}
}
