package com.ecommerce.shopping.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ecommerce.shopping.enums.UserRole;
import com.ecommerce.shopping.requestdto.AuthRequest;
import com.ecommerce.shopping.requestdto.OtpVerificationRequest;
import com.ecommerce.shopping.requestdto.UserRequest;
import com.ecommerce.shopping.responsedto.AuthResponse;
import com.ecommerce.shopping.responsedto.UserResponse;
import com.ecommerce.shopping.service.UserService;
import com.ecommerce.shopping.utility.ResponseStructure;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/version1")
@AllArgsConstructor
public class UserController {
	
	private UserService userService;

	@PostMapping("/sellers/register")
	public ResponseEntity<ResponseStructure<UserResponse>> saveSeller(@RequestBody UserRequest userRequest){
		
		return userService.saveUser(userRequest,UserRole.SELLER);
	}
	
	@PostMapping("/customers/register")
	public ResponseEntity<ResponseStructure<UserResponse>> saveCustomer(@RequestBody UserRequest userRequest){
		
		return userService.saveUser(userRequest,UserRole.CUSTOMER);
	}
	
	@PostMapping("/verifyotp")
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOtp(@RequestBody OtpVerificationRequest otpVerificationRequest){
		
		return userService.verifyOtp(otpVerificationRequest);
	}
	
	
	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<AuthResponse>> loginUser(@RequestBody AuthRequest authRequest) {
		
		return userService.loginUSer(authRequest);
	}
	
	@PostMapping("/refresh-logins")
	public ResponseEntity<ResponseStructure<AuthResponse>> refreshLogin(@CookieValue(value = "rt",required =false) String refreshtoken){
		
		return userService.refreshLogin(refreshtoken);
		
	}
	
	
	@GetMapping("/test")
	public String test() {
		return "Success ";
	}
	

}
