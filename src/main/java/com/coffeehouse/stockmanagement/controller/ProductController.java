package com.coffeehouse.stockmanagement.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity.BodyBuilder;

import com.coffeehouse.stockmanagement.entity.Product;
import com.coffeehouse.stockmanagement.service.ProductService;
import com.coffeehouse.stockmanagement.utils.Constants;

@CrossOrigin
@RestController
@RequestMapping(value = "/products")
public class ProductController {

	@Autowired 
	private ProductService service;
	
	@GetMapping
	public ResponseEntity<List<Product>> findAll() {
		List<Product> list = service.findAll();
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<Product> findById(@PathVariable Long id) {
		Product obj = service.findById(id);
		return ResponseEntity.ok().body(obj);
	}
	
	@PostMapping
	public ResponseEntity<?> insert(@RequestBody Product obj) {
		
		if(service.maximumReached(obj)) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Constants.QUANTITY_EXCEEDED);
		}
		
		obj = service.insert(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(obj.getId()).toUri();
		return ResponseEntity.created(uri).body(obj);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.ok().build();
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Product obj) {
		
		if(service.maximumReached(obj)) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Constants.QUANTITY_EXCEEDED);
		}
		
		obj = service.update(id, obj);
		return ResponseEntity.ok().body(obj);
	}

	@PutMapping("/upload/{id}")
	public ResponseEntity<?> uplaodImage(@PathVariable Long id, @RequestParam("imageFile") MultipartFile file) throws IOException {
		service.uploadImage(id, file.getBytes());
		return ResponseEntity.ok().build();
	}
}
