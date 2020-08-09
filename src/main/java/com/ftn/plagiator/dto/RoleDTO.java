
package com.ftn.plagiator.dto;

import com.ftn.plagiator.model.Role;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RoleDTO {

	private Long id;
	private String userType;
	
	public RoleDTO(Role role) {
		this.id = role.getId();
		this.userType = role.getUserType();
	}

}