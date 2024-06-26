package com.ecommerce.shopping.serviceimpl;
import java.util.Date;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ecommerce.shopping.entity.Customer;
import com.ecommerce.shopping.entity.Seller;
import com.ecommerce.shopping.entity.User;
import com.ecommerce.shopping.enums.UserRole;
import com.ecommerce.shopping.mapper.UserMapper;
import com.ecommerce.shopping.repository.UserRepository;
import com.ecommerce.shopping.requestdto.OtpVerificationRequest;
import com.ecommerce.shopping.requestdto.UserRequest;
import com.ecommerce.shopping.responsedto.UserResponse;
import com.ecommerce.shopping.service.UserService;
import com.ecommerce.shopping.utility.MessageData;
import com.ecommerce.shopping.utility.ResponseStructure;
import com.google.common.cache.Cache;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;


@Service
public class UserServiceImpl implements UserService {


	private final MailService mailService;

	private final Cache<String, User> userCache;

	private final Cache<String,String> optCache;

	private final UserRepository userRepository;

	private final UserMapper userMapper;

	private final Random random;

	private final MessageData messageData;



	//   by using contsructer we initialize the serviceimpl oject and we can initialize also by using the @AllArgsConstructor
	//	public UserServiceImpl(Cache<String, User> userCache) {
	//		this.userCache = userCache;
	//	}
	public UserServiceImpl(MailService mailService, Cache<String, User> userCache, Cache<String, String> optCache,
			UserRepository userRepository, UserMapper userMapper, Random random,MessageData messageData) {
		super();
		this.mailService = mailService;
		this.userCache = userCache;
		this.optCache = optCache;
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.random = random;
		this.messageData=messageData;
	}




	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> saveUser(UserRequest userRequest, UserRole userRole) {
		User user = null;

		switch (userRole) {
		case SELLER->{ user = new Seller();
		user.setUserRole(userRole);
		}
		case CUSTOMER-> { user = new Customer();
		user.setUserRole(userRole);
		}
		}
		if(user!=null) {

			user=userMapper.mapToUserRequest(userRequest, user);
		}


		userCache.put(user.getEmail(), user);


		int  number= random.nextInt(100000,999999);
		String randomNUmber = String.valueOf(number);

		optCache.put(user.getEmail(), randomNUmber);

		messageData.setTo(user.getEmail());
		messageData.setSubject("Your Otp is:");
		messageData.setText("otp is "+randomNUmber);
		messageData.setSentDate( new Date(System.currentTimeMillis()));

		try {
			mailService.sendMail(messageData);
		} catch (MessagingException e) {

			e.printStackTrace();
		}

		return	ResponseEntity.status(HttpStatus.ACCEPTED)
				.body(new ResponseStructure<UserResponse>()
						.setData(userMapper.mapToUserresponse(user))
						.setMessage("added  in cache ")
						.setStatus(HttpStatus.ACCEPTED.value()));	
	}



	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOtp(OtpVerificationRequest otpVerificationRequest) {


		User user  = userCache.getIfPresent(otpVerificationRequest.getEmail());
		String otp = optCache.getIfPresent(otpVerificationRequest.getEmail());

		String email = otpVerificationRequest.getEmail();
		
		if(email==null||email.contains("@gmail.com")) {
			throw new IllegalArgumentException("Inavlid email id ");
		}
		int atIndex=email.indexOf("@");
		if
		(atIndex==-1) {
			throw new IllegalArgumentException("invlid email address format");
		}
		String name = email.substring(0,atIndex);

		if(user==null||otp==null) {
			return 	ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseStructure<UserResponse>()
							.setMessage("Invalid OTP User not found")
							.setStatus(HttpStatus.NOT_FOUND.value()));
		}
		if(otpVerificationRequest.getOtp().equals(otp)) {
			user.setEmialVerified(true);
			user.setUsername(name);
			userRepository.save(user);

			return 	ResponseEntity.status(HttpStatus.FOUND)
					.body(new ResponseStructure<UserResponse>()
							.setMessage("user saved")
							.setData(userMapper.mapToUserresponse(user))
							.setStatus(HttpStatus.FOUND.value()));

		}
		else {

			return 	ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseStructure<UserResponse>()
							.setMessage("invalid otp")
							.setStatus(HttpStatus.NOT_FOUND.value()));
		}

	}

}
