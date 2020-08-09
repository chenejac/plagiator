package com.ftn.plagiator.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ftn.plagiator.dto.PaperDTO;
import com.ftn.plagiator.dto.PaperResultPlagiatorDTO;
import com.ftn.plagiator.dto.ResultItemDTO;
import com.ftn.plagiator.dto.UserDTO;
import com.ftn.plagiator.elasticsearch.model.PaperElastic;
import com.ftn.plagiator.elasticsearch.repository.PaperElasticRepository;
import com.ftn.plagiator.model.AdvancePaper;
import com.ftn.plagiator.model.Paper;
import com.ftn.plagiator.model.PaperResultPlagiator;
import com.ftn.plagiator.model.ResultItem;
import com.ftn.plagiator.model.User;
import com.ftn.plagiator.service.AdvancePaperService;
import com.ftn.plagiator.service.EmailService;
import com.ftn.plagiator.service.PaperResultPlagiatorService;
import com.ftn.plagiator.service.PaperService;
import com.ftn.plagiator.service.ResultItemService;
import com.ftn.plagiator.service.SearchService;
import com.ftn.plagiator.service.UserService;
import com.ftn.plagiator.util.FileClass;
import com.ftn.plagiator.util.HelpersFunctions;
import com.ftn.plagiator.util.ObjectMapperUtil;
import com.ftn.plagiator.util.RoleConstants;
import com.ftn.plagiator.validation.StaticData;

@Controller
public class UploadFileController {
	
	@Autowired
	PaperService paperService;
	
	@Autowired
	SearchService searchService;

	@Autowired
	PaperElasticRepository paperElasticRepository;
	
	@Autowired
	ObjectMapperUtil objectMapper;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	PaperResultPlagiatorService paperResultPlagiatorService;
	
	@Autowired
	ResultItemService resultItemService;
	
	@Autowired
	AdvancePaperService advancePaperService;
  
    @RequestMapping(value = "api/file/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaperDTO> addPaper(@ModelAttribute PaperDTO paperDTO, HttpServletRequest request) {
    	
		Principal principal = request.getUserPrincipal();
        String email = principal.getName();
        User logged = userService.findByEmail(email);

    	PaperElastic paperElastic = this.uploadFileAndSavePaperElastic(paperDTO, logged);
    	if(paperElastic == null) {
    		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}

		return new ResponseEntity<>(objectMapper.map(paperElastic, PaperDTO.class), HttpStatus.OK);
	}
    
    @RequestMapping(value = "api/file/upload/new", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaperResultPlagiatorDTO> addPaperNew(@ModelAttribute PaperDTO paperDTO, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();
        String email = principal.getName();
        User logged = userService.findByEmail(email);
    	
    	PaperElastic paperElastic = this.uploadFileAndSavePaperElastic(paperDTO, logged);
    	if(paperElastic == null) {
    		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}
    	
    	
    	//uploadovao je uspesno novi rad sad da vidimo koji slicni radovi postoje
    	Paper paper = paperService.findOne(paperElastic.getId());
		File file = new File(paper.getPathForPDF());
    	String stringParam =  FileClass.getTextFromFile(paperDTO.getFile().getOriginalFilename(), file);
		//System.out.println(stringParam);
		
		
    	String[] wordList = stringParam.split("[\\s\\-\\.\\'\\?\\,\\_\\@\\;\\:]+"); //splituj po space-u
		System.out.println(wordList.length);
		
		StringBuilder strBilder = new StringBuilder();
		PaperResultPlagiatorDTO paperResultPlagiator = new PaperResultPlagiatorDTO();
		Set<PaperDTO> setSimilarPapers = new HashSet<PaperDTO>();
		int partOfPage = 0;
		
		for(int i=1; i <= wordList.length; ++i) {
			
			strBilder.append(wordList[i-1]);
			strBilder.append(" ");
			
			if(i % StaticData.NUMBERS_OF_WORDS_SPLITER == 0 || i == wordList.length) {
				//dosao sam do prvog parceta teksta odnosno poslednjeg
				StringBuilder textToShowBilder = new StringBuilder();
				
				Page<PaperElastic> papers = searchService.listaRadova(strBilder.toString());
				strBilder = new StringBuilder();
				++partOfPage;
				
				List<PaperDTO> retVal = new ArrayList<PaperDTO>();
				int counter = 0;
				
				if(papers.hasContent()) { //ukoliko ima bar jedan dokument	
					for (PaperElastic item : papers) {
						
						Paper foundPaper = paperService.findOne(item.getId());
						PaperDTO paperDTORet = objectMapper.map(item, PaperDTO.class);
						paperDTORet.setUser(objectMapper.map(foundPaper.getUser(), UserDTO.class));
						
						//iskljuciti dodavanje istog
						if(item.getId() != paper.getId()) {
							//paperResultPlagiator.getSimilarPapers().add(paperDTORet);
							setSimilarPapers.add(paperDTORet);
						}
						
						retVal.add(objectMapper.map(item, PaperDTO.class));
						
						++counter;
						if(counter == StaticData.NUMBERS_OF_FILES) {
							break;
						}
					}
				}
				
				try {
					for(int j=0; j < StaticData.NUMBERS_OF_WORDS_TO_SHOW; ++j) {
						textToShowBilder.append(wordList[(partOfPage-1) * StaticData.NUMBERS_OF_WORDS_SPLITER + j]);
						textToShowBilder.append(" ");
					}
				} catch(Exception E) {
					System.out.println("nece imati sve reci na kraju");
				}
				
				ResultItemDTO resultItem = new ResultItemDTO();
				//sortiraj i konvertuj u listu
				//List<PaperDTO> sortedList = new ArrayList<>(ret);
				//Collections.sort(sortedList, new PaperDTO());
				resultItem.setPapers(retVal);
				resultItem.setPartOfPage(partOfPage);
				resultItem.setTextToShow(textToShowBilder.toString());
				paperResultPlagiator.getItems().add(resultItem);
				paperResultPlagiator.setUploadedPaper(objectMapper.map(paper, PaperDTO.class));
			}
		}
		
		paperResultPlagiator.setSimilarPapers(this.returnListOdSimilarPapers(
				setSimilarPapers, paperResultPlagiator.getItems()));
		
		PaperResultPlagiator plagiator = this.convertToSavePlagiarismForPaper(paperResultPlagiator);
		
		//poslati neki mail ako je potrebno
        
		try {
			emailService.sendNotificaitionUploadOfNewDocument(logged, paperDTO.getFile().getOriginalFilename(), plagiator.getId());
		} catch (MailException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return new ResponseEntity<>(paperResultPlagiator, HttpStatus.OK);
	}
    
    @RequestMapping(value = "api/file/upload/new/results/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PaperResultPlagiatorDTO> checkResultsPaper(@PathVariable Long id, HttpServletRequest request) {
    	PaperResultPlagiator plagiator = paperResultPlagiatorService.findOne(id);
    	
    	//ako ne postoji taj rad
    	if(plagiator == null) {
    		return new ResponseEntity<>(HttpStatus.LOCKED);
    	}
    	
    	//obrisan je rad
    	if(plagiator.getUploadedPaper() == null) {
    		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}
    	
		Principal principal = request.getUserPrincipal();
        String email = principal.getName();
        User logged = userService.findByEmail(email);
    	
    	//nije od tog korisnika pa ne moze da ga vidi i korisnikova uloga nije admin
    	if(!logged.getRole().getUserType().equals(RoleConstants.ROLE_ADMIN) //ako je admin nek prodje
    			&& !plagiator.getUploadedPaper().getUser().getEmail().equals(email)) { //nisi ga ti uplodovao
    		return new ResponseEntity<>(HttpStatus.LOCKED);
    	}
    	
    	PaperResultPlagiatorDTO plagiatorDTO = new PaperResultPlagiatorDTO();
    	plagiatorDTO.setId(plagiator.getId());
    	plagiatorDTO.setUploadedPaper(objectMapper.map(plagiator.getUploadedPaper(), PaperDTO.class));
    	
		Set<PaperDTO> setSimilarPapers = new HashSet<PaperDTO>();
    	
    	for(ResultItem resultItem: plagiator.getItems()) {
    		ResultItemDTO resultItemDTO = new ResultItemDTO();
    		resultItemDTO.setPartOfPage(resultItem.getPartOfPage());
    		resultItemDTO.setTextToShow(resultItem.getTextToShow());
    		
    		List<AdvancePaper> advances = advancePaperService.findByResultItemId(resultItem.getId());
    		for(AdvancePaper advancePaper: advances) {
    			if(resultItemDTO.getPapers() == null) {
    				resultItemDTO.setPapers(new ArrayList<PaperDTO>());
    			}
    			
    			PaperDTO paperDTO = objectMapper.map(advancePaper.getPaper(), PaperDTO.class);
    			paperDTO.setSearchHits(advancePaper.getSearchHits());

    			resultItemDTO.getPapers().add(paperDTO);
    			
				//iskljuciti dodavanje istog
				if(paperDTO.getId() != plagiatorDTO.getUploadedPaper().getId()) {
					//paperResultPlagiator.getSimilarPapers().add(paperDTORet);
					setSimilarPapers.add(paperDTO);
				}
    		}
    		
    		plagiatorDTO.getItems().add(resultItemDTO);
    	}
    	
		Collections.sort(plagiatorDTO.getItems(), new ResultItemDTO());
    	plagiatorDTO.setSimilarPapers(this.returnListOdSimilarPapers(
				setSimilarPapers, plagiatorDTO.getItems()));
    	
    	return new ResponseEntity<PaperResultPlagiatorDTO>(plagiatorDTO, HttpStatus.OK);
    }
    
	@RequestMapping(value = "api/file/download/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<byte[]> download(@PathVariable Long id, HttpServletResponse response) {

		Paper paper = paperService.findOne(id);

		if (paper == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);

		byte[] content;

		try {
			File paperPdf = new File(paper.getPathForPDF());
			//System.out.println(paper.getPathForPDF());
			content = Files.readAllBytes(paperPdf.toPath());
			headers.setContentDispositionFormData(paperPdf.getName(), paperPdf.getName());
			headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		} catch (IOException e) {

			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<byte[]>(content, headers, HttpStatus.OK);
	}
	
	private PaperElastic uploadFileAndSavePaperElastic(PaperDTO paperDTO, User logged) {
		String path = "";
		try {
			path = FileClass.saveFile(paperDTO.getFile(), StaticData.PUTANJA_DO_FAJLA);
		} catch (IOException e) {

			return null; 
		}

		paperDTO.setPathForPDF(path);
		paperDTO.setTitle(paperDTO.getFile().getOriginalFilename());
		//paperDTO.setTitle(paperDTO.getFile().getOriginalFilename().split(".")[0]);
		
		//do something with file
		String text = "";
		File file = new File(path);
		text = FileClass.getTextFromFile(paperDTO.getFile().getOriginalFilename(), file);
		//System.out.println(text);
		//do something with file
		
		Paper paper = new Paper(paperDTO);
		paper.setUser(logged);
		paper = paperService.save(paper);
		
		PaperElastic paperElastic = new PaperElastic();
		paperElastic.setId(paper.getId());
		paperElastic.setText(text);
		paperElastic.setTitle(paper.getTitle());
		paperElastic = paperElasticRepository.save(paperElastic);
		
		return paperElastic;
	}
	
	private List<PaperDTO> returnListOdSimilarPapers(Set<PaperDTO> similarPapers, List<ResultItemDTO> items) {
		
		for(PaperDTO paperDTO: similarPapers) {
			
			double sum = 0;
			
			for(ResultItemDTO item: items) {
				double tempFactor = this.returnProcentOfSimilarityOfItem(item, paperDTO);
				double tempToAdd = HelpersFunctions.calculateCoefficient(tempFactor);
				sum += tempToAdd;
			}
			
			paperDTO.setSimilarProcent(sum / items.size()); //srednja vrednost od svih itema sa koeficijentima
		}
	
		//sortiraj i konvertuj u listu
		List<PaperDTO> sortedList = new ArrayList<>(similarPapers);
		Collections.sort(sortedList, new PaperDTO());
		return sortedList;

	}
	
	private double returnProcentOfSimilarityOfItem(ResultItemDTO item, PaperDTO paperDTO) {
		double maxValue = 0;
		for(PaperDTO paper: item.getPapers()) {
			
			if(maxValue < paper.getSearchHits()) {
				maxValue = paper.getSearchHits();
			}
			
		}
		
		for(PaperDTO paper: item.getPapers()) {
			if(paper.getId() == paperDTO.getId()) {
				return paper.getSearchHits() / maxValue; //da bi dobio procenat podelim sa najvecim mogucim
			}
		}
		
		return 0;
	}
	
	private PaperResultPlagiator convertToSavePlagiarismForPaper(PaperResultPlagiatorDTO plagiatorDTO) {
		PaperResultPlagiator plagiator = new PaperResultPlagiator();
		Paper paper = new Paper();
		paper.setId(plagiatorDTO.getUploadedPaper().getId());
		plagiator.setUploadedPaper(paper);
		plagiator = paperResultPlagiatorService.save(plagiator);
		
		//sad sacuvaj iteme za njega
		for(ResultItemDTO resultItemDTO: plagiatorDTO.getItems()) {
			
			ResultItem resultItem = new ResultItem();
			resultItem.setPaperResultPlagiator(plagiator);
			resultItem.setTextToShow(resultItemDTO.getTextToShow());
			resultItem.setPartOfPage(resultItemDTO.getPartOfPage());
			resultItem = resultItemService.save(resultItem);
			
			
			for(PaperDTO paperDTO: resultItemDTO.getPapers()) {
							
				Paper p = paperService.findOne(paperDTO.getId());
				AdvancePaper ap = new AdvancePaper();
				ap.setPaper(p);
				ap.setSearchHits(paperDTO.getSearchHits());
				ap.setResultItem(resultItem);
				ap = advancePaperService.save(ap);
			}
			
		}
		
		return plagiator;
	}

}
