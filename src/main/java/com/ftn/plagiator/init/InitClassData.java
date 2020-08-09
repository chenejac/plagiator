package com.ftn.plagiator.init;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ftn.plagiator.model.Role;
import com.ftn.plagiator.model.User;
import com.ftn.plagiator.repository.RoleRepository;
import com.ftn.plagiator.service.UserService;
import com.ftn.plagiator.util.RoleConstants;

@Component
public class InitClassData {
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	UserService userService;
	
	@PostConstruct
	public void start() {
		Optional<Role> paramToRun = roleRepository.findById(1L);
		
		if(paramToRun.isPresent()) {
			return;
		}
		
		//ROLES
		Role roleLogged = new Role(1L, RoleConstants.ROLE_LOGGED);
		roleLogged = roleRepository.save(roleLogged);
		Role roleAdmin = new Role(2L, RoleConstants.ROLE_ADMIN);
		roleAdmin = roleRepository.save(roleAdmin);
		
		//USERS
		User user1 = new User();
		user1.setId(1L);
		user1.setName("admin");
		user1.setLastName("admin");
		user1.setActive(true);
		user1.setEmail("chenejac@uns.ac.rs");
		user1.setPassword("12345"); //treba heshovati
		user1.setRole(roleAdmin);
		user1 = userService.signup(user1);

		User user2 = new User();
		user2.setId(2L);
		user2.setName("Dragan");
		user2.setLastName("Ivanovic");
		user2.setActive(true);
		user2.setEmail("dragan.ivanovic@uns.ac.rs");
		user2.setPassword("12345"); //treba heshovati
		user2.setRole(roleLogged);
		user2 = userService.signup(user2);
	}

}
