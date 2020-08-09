package com.ftn.plagiator.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ResultItem{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "resultItem")
	private List<AdvancePaper> papers;
    
	private int partOfPage;
	private String textToShow;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private PaperResultPlagiator paperResultPlagiator;

}
