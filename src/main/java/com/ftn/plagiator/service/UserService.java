package com.ftn.plagiator.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ftn.plagiator.exceptions.CustomException;
import com.ftn.plagiator.jwt.JwtTokenUtils;
import com.ftn.plagiator.model.User;
import com.ftn.plagiator.repository.UserRepository;


@Service
public class UserService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtTokenUtils jwtTokenProvider;

	@Autowired
	private AuthenticationManager authenticationManager;

	public Page<User> findAll(Pageable page) {
		return userRepository.findAll(page);
	}

	public User findOne(Long id) {
		return userRepository.findById(id).get();
	}

	public User save(User korisnik) {
		return userRepository.save(korisnik);
	}

	public void remove(Long id) {
		userRepository.deleteById(id);
	}


	public String signin(String email, String lozinka) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, lozinka));
			return jwtTokenProvider.createToken(email, userRepository.findByEmail(email).getRole());
		} catch (AuthenticationException e) {
			throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	public User signup(User korisnik) {
		if (!userRepository.existsByEmail(korisnik.getEmail())) {
			korisnik.setPassword(passwordEncoder.encode(korisnik.getPassword()));
			return userRepository.save(korisnik);
		} else {
			throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
	
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	public User changeState(Long id, boolean boolState) {
		Optional<User> user = userRepository.findById(id);
		if (user.isPresent()) {
			user.get().setActive(boolState);
			User retVal = userRepository.save(user.get());
			return retVal;
		} else {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT,
					"Requested user with id " + id + " doesn't exist.");
		}
	}
}
