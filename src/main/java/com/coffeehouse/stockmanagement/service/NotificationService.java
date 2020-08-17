package com.coffeehouse.stockmanagement.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.coffeehouse.stockmanagement.entity.Notification;
import com.coffeehouse.stockmanagement.entity.Product;
import com.coffeehouse.stockmanagement.entity.User;
import com.coffeehouse.stockmanagement.exceptions.DatabaseException;
import com.coffeehouse.stockmanagement.exceptions.ResourceNotFoundException;
import com.coffeehouse.stockmanagement.repository.NotificationRepository;
import com.coffeehouse.stockmanagement.repository.ProductRepository;
import com.coffeehouse.stockmanagement.repository.UserRepository;
import com.coffeehouse.stockmanagement.utils.Constants;

@Service
public class NotificationService {
	
	@Autowired
	private NotificationRepository repository;

	public List<Notification> findByUserId(Long id) {
		return repository.findByUserIdOrderByCreationDateDesc(id);
	}
	
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}
	
}
