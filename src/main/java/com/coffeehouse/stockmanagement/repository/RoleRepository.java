package com.coffeehouse.stockmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coffeehouse.stockmanagement.entity.Role;
import com.coffeehouse.stockmanagement.enums.ERole;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(ERole name);
	
	Boolean existsByName(ERole name);
}