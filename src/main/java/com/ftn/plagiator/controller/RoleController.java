package com.ftn.plagiator.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ftn.plagiator.dto.RoleDTO;
import com.ftn.plagiator.model.Role;
import com.ftn.plagiator.service.RoleService;
import com.ftn.plagiator.util.ObjectMapperUtil;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/roles")
public class RoleController {

	@Autowired
	private RoleService roleService;
	
	@Autowired
	ObjectMapperUtil objectMapper;

	@RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<RoleDTO>> getAllRoles(Pageable pageable) {
		List<RoleDTO> found = roleService.findAll(pageable);		
		HttpHeaders headers = new HttpHeaders();
		long rolesTotal = found.size();
		headers.add("X-Total-Count", String.valueOf(rolesTotal));
		
		return new ResponseEntity<List<RoleDTO>>(found, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}",method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<RoleDTO> getRole(@PathVariable("id") Long id) {
		return new ResponseEntity<RoleDTO>(objectMapper.map(roleService.findOne(id), RoleDTO.class), HttpStatus.OK);
	}
	
	@RequestMapping( method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO roleDTO) {
		return new ResponseEntity<RoleDTO>(new RoleDTO(roleService.
				save(objectMapper.map(roleDTO, Role.class))), HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<RoleDTO> updateRole(@PathVariable("id") Long id, @RequestBody RoleDTO roleDTO){
		return new ResponseEntity<RoleDTO>(new RoleDTO(roleService.
				update(id, objectMapper.map(roleDTO, Role.class))), HttpStatus.ACCEPTED);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> deleteRole(@PathVariable("id") Long id) {
		roleService.remove(id);
		return ResponseEntity.ok().build();
	}
}