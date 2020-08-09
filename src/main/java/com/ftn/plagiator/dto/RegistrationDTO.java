package com.ftn.plagiator.dto;

import com.ftn.plagiator.model.Role;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RegistrationDTO {
	private Long id;
	private String name;
	private String lastName;
	private String password;
	private String repeatedPassword;
	private String email;
	private String phoneNumber;
	private Role role;

}
