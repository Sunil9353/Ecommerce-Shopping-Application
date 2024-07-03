package com.ecommerce.shopping.service;

import org.springframework.http.ResponseEntity;

import com.ecommerce.shopping.enums.UserRole;
import com.ecommerce.shopping.requestdto.AuthRequest;
import com.ecommerce.shopping.requestdto.OtpVerificationRequest;
import com.ecommerce.shopping.requestdto.UserRequest;
import com.ecommerce.shopping.responsedto.AuthResponse;
import com.ecommerce.shopping.responsedto.UserResponse;
import com.ecommerce.shopping.utility.ResponseStructure;

public interface UserService {

	ResponseEntity<ResponseStructure<UserResponse>> saveUser(UserRequest userRequest, UserRole userRole);

	ResponseEntity<ResponseStructure<UserResponse>> verifyOtp(OtpVerificationRequest otpVerificationRequest);

	ResponseEntity<ResponseStructure<AuthResponse>> loginUSer(AuthRequest authRequest);

	

	ResponseEntity<ResponseStructure<AuthResponse>> refreshLogin(String refreshtoken);

}
