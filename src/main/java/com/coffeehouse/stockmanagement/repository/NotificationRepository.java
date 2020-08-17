package com.coffeehouse.stockmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coffeehouse.stockmanagement.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	
	List<Notification> findByUserIdOrderByCreationDateDesc(Long id);

}
