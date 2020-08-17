package com.coffeehouse.stockmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coffeehouse.stockmanagement.entity.Notification;
import com.coffeehouse.stockmanagement.service.NotificationService;

@CrossOrigin
@RestController
@RequestMapping(value = "/notifications")
public class NotificationController {

	@Autowired 
	private NotificationService service;

	@GetMapping(value = "/{id}")
	public ResponseEntity<List<Notification>> findByUserId(@PathVariable Long id) {
		List<Notification> list = service.findByUserId(id);
		return ResponseEntity.ok().body(list);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.ok().build();
	}

}
