package com.coffeehouse.stockmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.coffeehouse.stockmanagement.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	Boolean existsByName(String name);
	
	@Query ( value = "select ifnull(sum(quantity), 0) from product where category in (select id from category where id = :id)", nativeQuery = true)
	int countByCategory(Long id);
}