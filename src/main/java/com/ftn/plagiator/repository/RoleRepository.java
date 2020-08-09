package com.ftn.plagiator.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ftn.plagiator.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{

	Role findByUserType(String roleName);
}
