package com.ftn.plagiator.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Report {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne()
	@JoinColumn(name = "paper_uploaded_id")
	Paper paperUploaded;
	
	@ManyToOne()
	@JoinColumn(name = "paper_to_check_id")
	Paper paperToCheck;
	
	@OneToMany(mappedBy = "report")
	Set<Comment> comments = new HashSet<Comment>();
	
	
}
