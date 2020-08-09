package com.ftn.plagiator.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Role{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String userType;
	
    public Role(Long id, String role){
    	this.id = id;
		this.userType = role;
	}

}
