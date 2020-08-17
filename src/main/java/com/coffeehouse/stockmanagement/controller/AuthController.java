package com.coffeehouse.stockmanagement.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coffeehouse.stockmanagement.entity.Role;
import com.coffeehouse.stockmanagement.entity.User;
import com.coffeehouse.stockmanagement.enums.ERole;
import com.coffeehouse.stockmanagement.payload.JwtResponse;
import com.coffeehouse.stockmanagement.payload.LoginRequest;
import com.coffeehouse.stockmanagement.payload.MessageResponse;
import com.coffeehouse.stockmanagement.payload.RegisterRequest;
import com.coffeehouse.stockmanagement.repository.RoleRepository;
import com.coffeehouse.stockmanagement.repository.UserRepository;
import com.coffeehouse.stockmanagement.security.jwt.JwtUtils;
import com.coffeehouse.stockmanagement.service.UserDetailsImpl;
import com.coffeehouse.stockmanagement.utils.Constants;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	JwtUtils jwtUtils;

	@PostMapping(value = "/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		System.out.println(encoder.encode(loginRequest.getPassword()));
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(),
												 roles));
	}
	
	
	@PostMapping(value = "/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
		if (userRepository.existsByUsername(registerRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		User user = new User(registerRequest.getUsername(), encoder.encode(registerRequest.getPassword()));

		Set<Role> roles = new HashSet<>();

		if(registerRequest.getRole().equals(Constants.ROLE_MANAGER)) {
			Role userRole = roleRepository.findByName(ERole.MANAGER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			Role userRole = roleRepository.findByName(ERole.EMPLOYEE).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		}
		
		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	
}
