package com.ftn.plagiator.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PaperResultPlagiatorDTO {
	private Long id;
	private List<ResultItemDTO> items = new ArrayList<ResultItemDTO>();
	private List<PaperDTO> similarPapers = new ArrayList<PaperDTO>();
	private PaperDTO uploadedPaper;
}
