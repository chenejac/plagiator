package com.ftn.plagiator.dto;

import java.util.List;

import com.ftn.plagiator.model.Paper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ReportDTO {
	private Long id;
	private Paper paperUploaded;
	private Paper paperToCheck;
	private List<CommentDTO> comments;
}
