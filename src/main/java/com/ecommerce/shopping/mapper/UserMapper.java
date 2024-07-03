package com.ecommerce.shopping.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ecommerce.shopping.entity.User;
import com.ecommerce.shopping.requestdto.UserRequest;
import com.ecommerce.shopping.responsedto.UserResponse;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserMapper {
	
	private PasswordEncoder passwordEncoder;
	
	public User mapToUserRequest(UserRequest userRequest,User user) {
		
		user.setEmail(userRequest.getEmail());
		user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		
		return user;
		
	}
	
	public UserResponse mapToUserresponse(User user) {
		
	return	UserResponse.builder().userId(user.getUserId())
			.username(user.getUsername())
			.email(user.getEmail())
			.userRole(user.getUserRole())
		    .isDeleted(user.isDeleted())
		    .isEmialVerified(user.isEmialVerified())
          .build();	
	}
	
	

}
