package com.ftn.plagiator.elasticsearch.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.ftn.plagiator.model.Paper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(indexName = "paper", type = "paper")
@NoArgsConstructor
@Getter
@Setter
public class PaperElastic { //IndexUnit osnovna jedinica indeksiranja
	@Id
	@Field(type = FieldType.Long)
	private Long id;
	
	@Field(type = FieldType.Text)
	private String title;
	
	@Field(type = FieldType.Text, analyzer = "serbian-analyzer", searchAnalyzer = "serbian-analyzer")
	private String content;
	
	@Field(type = FieldType.Text, analyzer = "serbian-analyzer", searchAnalyzer = "serbian-analyzer")
	private String text;
	
	private double searchHits;
	
	
	public PaperElastic(Paper paper) {
		this.id = paper.getId();
		this.title = paper.getTitle();
	}
	
}
