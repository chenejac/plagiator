package com.ftn.plagiator.dto;

import com.ftn.plagiator.model.Role;
import com.ftn.plagiator.model.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
	private Long id;
    private String name;
    private String lastName;
    private String email;
	private String phoneNumber;
	private boolean active;
	private Role role;
	
	public UserDTO(User user) {
		this.id = user.getId();
		this.name = user.getName();
		this.lastName = user.getLastName();
		this.email = user.getEmail();
		this.phoneNumber = user.getPhoneNumber();
		this.active = user.isActive();
		this.role = user.getRole();
	}
}
