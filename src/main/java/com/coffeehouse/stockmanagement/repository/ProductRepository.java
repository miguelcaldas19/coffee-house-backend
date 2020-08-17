package com.coffeehouse.stockmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.coffeehouse.stockmanagement.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	
	@Transactional
    @Modifying
    @Query("update Product product set product.image = :image where product.id = :id")
    void upploadImage(@Param("id") Long id, @Param("image") byte[] image);
}
