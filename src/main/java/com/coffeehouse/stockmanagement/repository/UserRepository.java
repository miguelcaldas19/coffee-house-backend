package com.coffeehouse.stockmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.coffeehouse.stockmanagement.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);
	
	@Query ( value = "select * from users user where user.id IN (select user_id from user_roles where role_id in (select id from roles where name = 'MANAGER'))", nativeQuery = true)
	List<User> findAllManagers();
}