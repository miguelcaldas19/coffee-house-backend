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

import com.coffeehouse.stockmanagement.entity.Category;
import com.coffeehouse.stockmanagement.entity.Notification;
import com.coffeehouse.stockmanagement.entity.Product;
import com.coffeehouse.stockmanagement.entity.User;
import com.coffeehouse.stockmanagement.exceptions.DatabaseException;
import com.coffeehouse.stockmanagement.exceptions.ResourceNotFoundException;
import com.coffeehouse.stockmanagement.repository.CategoryRepository;
import com.coffeehouse.stockmanagement.repository.NotificationRepository;
import com.coffeehouse.stockmanagement.repository.ProductRepository;
import com.coffeehouse.stockmanagement.repository.UserRepository;
import com.coffeehouse.stockmanagement.utils.Constants;
import com.coffeehouse.stockmanagement.utils.ImageOperations;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;

	public List<Product> findAll() {
		
		List<Product> list = productRepository.findAll();
		
		for(Product item : list) {
			if(item.getImage() != null) {
				byte[] imageDecompress = ImageOperations.decompressBytes(item.getImage());
				item.setImage(imageDecompress);
			}
		}
		
		return productRepository.findAll();
	}

	public Product findById(Long id) {
		Optional<Product> obj = productRepository.findById(id);
		return obj.orElseThrow(() -> new ResourceNotFoundException(id));
	}
	
	public Product insert(Product obj) {
		
		validateMinQuantity(obj);
		
		return productRepository.save(obj);
	}
	
	public void delete(Long id) {
		try {
			productRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}
	
	public Product update(Long id, Product obj) {
		try {
			
			validateMinQuantity(obj);
			validateStockReffiled(obj);
			
			Product entity = productRepository.getOne(id);
			updateData(entity, obj);
			return productRepository.save(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		}	
	}
	
	private void updateData(Product entity, Product obj) {
		entity.setName(obj.getName());
		entity.setDescription(obj.getDescription());
		entity.setQuantity(obj.getQuantity());
	}
	
	/*
	 * validate if the product gets under of the minimum quantity
	 * generate notification to all users
	 */
	
	private void validateMinQuantity(Product obj) {
		
		if (obj.getQuantity() < Constants.MIN_QUANTITY_VALUE) {

			List<User> users = userRepository.findAll();
			
			for(User user : users) {
				Notification notification = new Notification();
				notification.setDescription("The " + obj.getName() + " stock is low. Current value: " + obj.getQuantity());
				notification.setUserId(user.getId());
				notification.setCreationDate(new Date());
				notificationRepository.save(notification);
			}	
		}
	}
	
	/*
	 * 	validates if the product has been refilled
	 * 	if the new quantity is bigger than the old quantity generate notification to all managers
	 */

	private void validateStockReffiled(Product obj) {
		
		Product product = productRepository.getOne(obj.getId());
		
		if (obj.getQuantity() > product.getQuantity()) {
					
			List<User> managers = userRepository.findAllManagers();
			
			for(User user : managers) {
				Notification notification = new Notification();
				notification.setDescription("The " + obj.getName() + " stock is refilled. Current value: " + obj.getQuantity());
				notification.setCreatedBy(((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
				notification.setUserId(user.getId());
				notification.setCreationDate(new Date());
				notificationRepository.save(notification);
			}	
		}
	}
	
	public void uploadImage(long id, byte[] image) {
		productRepository.upploadImage(id, ImageOperations.compressBytes(image));
	}
	
	public Boolean maximumReached(Product obj) {
		
		Optional<Category> category = categoryRepository.findById(obj.getCategory());
		int oldQuantity = 0;
		
		if(obj.getId() != null) {
			Optional<Product> oldProduct = productRepository.findById(obj.getId());
			oldQuantity = oldProduct.get().getQuantity();
		};
		
		if(category.get().getName().equals(Constants.CATEGORY_TYPE_COFFEE)) {	
			int sumCoffee = categoryRepository.countByCategory(obj.getCategory());
			if (sumCoffee + obj.getQuantity() - oldQuantity > Constants.MAX_QUANTITY_COFFEE) {
				return true;
			}
		}
		
		if(category.get().getName().equals(Constants.CATEGORY_TYPE_TEA)) {	
			int sumCoffee = categoryRepository.countByCategory(obj.getCategory());
			if (sumCoffee + obj.getQuantity() - oldQuantity> Constants.MAX_QUANTITY_TEA) {
				return true;
			}
		}
		
		return false;

	}
	
	
}
