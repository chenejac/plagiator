package com.ftn.plagiator.dto;

import java.time.Instant;

import com.ftn.plagiator.model.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CommentDTO {
	private Long id;
	private String text;
	private User user;
	private Instant datetime;
	private int matcheNumber; // skala od 1 do 5
	
	private String plagiatWith;
}
