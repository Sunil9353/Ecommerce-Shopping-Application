package com.ecommerce.shopping.serviceimpl;
import java.time.LocalDateTime;
import java.util.Date;

import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.shopping.entity.AccessToken;
import com.ecommerce.shopping.entity.Customer;
import com.ecommerce.shopping.entity.RefreshToken;
import com.ecommerce.shopping.entity.Seller;
import com.ecommerce.shopping.entity.User;
import com.ecommerce.shopping.enums.UserRole;
import com.ecommerce.shopping.exception.AuthenticationFailed;
import com.ecommerce.shopping.exception.InvalidTokenException;
import com.ecommerce.shopping.exception.UserNotFoundException;
import com.ecommerce.shopping.mapper.UserMapper;
import com.ecommerce.shopping.repository.AccessTokenRepository;
import com.ecommerce.shopping.repository.RefreshTokenRepository;
import com.ecommerce.shopping.repository.UserRepository;
import com.ecommerce.shopping.requestdto.AuthRequest;
import com.ecommerce.shopping.requestdto.OtpVerificationRequest;
import com.ecommerce.shopping.requestdto.UserRequest;
import com.ecommerce.shopping.responsedto.AuthResponse;
import com.ecommerce.shopping.responsedto.UserResponse;
import com.ecommerce.shopping.service.UserService;
import com.ecommerce.shopping.utility.MessageData;
import com.ecommerce.shopping.utility.ResponseStructure;
import com.google.common.cache.Cache;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.val;


@Service
public class UserServiceImpl implements UserService {


	private final MailService mailService;

	private final Cache<String, User> userCache;

	private final Cache<String,String> optCache;

	private final UserRepository userRepository;

	private final UserMapper userMapper;

	private final Random random;

	private final JWtService jWtService;

	private final AuthenticationManager authenticationManager;

	private AccessTokenRepository accessTokenRepository;

	private RefreshTokenRepository refreshTokenRepository;


	@Value("${application.jwt.access_expiry_seconds}")
	private long accessExpirySeconds;

	@Value("${application.jwt.refresh_expiry_seconds}")
	private long refreshExpirySeconds;

	@Value("${application.cookie.domain}")
	private String domain;


	@Value("${application.cookie.same_site}")
	private String sameSite;

	@Value("${application.cookie.secure}")
	private boolean secure;



	public UserServiceImpl(MailService mailService, Cache<String, User> userCache, Cache<String, String> optCache,
			UserRepository userRepository, UserMapper userMapper, Random random, JWtService jWtService,
			AuthenticationManager authenticationManager, AccessTokenRepository accessTokenRepository,
			RefreshTokenRepository refreshTokenRepository) {
		super();
		this.mailService = mailService;
		this.userCache = userCache;
		this.optCache = optCache;
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.random = random;
		this.jWtService = jWtService;
		this.authenticationManager = authenticationManager;
		this.accessTokenRepository = accessTokenRepository;
		this.refreshTokenRepository = refreshTokenRepository;
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

		MessageData messageData = MessageData.builder().to(user.getEmail()).subject("Your Otp is:")
				.text("otp ois"+randomNUmber)
				.sentDate(new Date(System.currentTimeMillis())).build();


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

		if(email==null||!email.contains("@gmail.com")) {
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




	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> loginUSer(AuthRequest authRequest) {
		Authentication authenticate = authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(authRequest.getUsername(),authRequest.getPassword()));

		if(authenticate.isAuthenticated()) {
			return userRepository.findByUsername(authRequest.getUsername()).map(user->{
				HttpHeaders headers = new HttpHeaders();
				grantAccessToken(headers, user);
				grantRefreshToken(headers, user);

				return ResponseEntity.ok().headers(headers).body(new ResponseStructure<AuthResponse>()
						.setStatus(HttpStatus.OK.value())
						.setMessage("login  successfully")
						.setData(AuthResponse.builder().userId(user.getUserId())
								.username(user.getUsername())
								.accssExpiration(accessExpirySeconds)
								.refreshExpiration(refreshExpirySeconds).build()));

			}).orElseThrow(()-> new UserNotFoundException(" User name is not found"));

		}
		else {
			throw new BadCredentialsException("jwt is not found");
		}

	}


	private String generateCookie(String name,String value,long maxAge) {


		return  ResponseCookie.from(name, value)
				.domain(domain)
				.path("/")
				.maxAge(maxAge)
				.sameSite(sameSite)
				.httpOnly(true)
				.secure(secure).build().toString();	

	}



	private  void  grantAccessToken(HttpHeaders headers,User user) {

		String jwtToken = jWtService.createJwtToken(user.getUsername(), accessExpirySeconds*1000, user.getUserRole().toString());

		AccessToken accessToken = AccessToken.builder().token(jwtToken)
				.expiration(LocalDateTime.now()
						.plusSeconds(accessExpirySeconds))
				.user(user)
				.build();	 
		accessTokenRepository.save(accessToken);

		headers.add(HttpHeaders.SET_COOKIE, generateCookie("at", jwtToken, accessExpirySeconds));

	}
	
	private void grantRefreshToken(HttpHeaders headers, User user) {

		String jwtToken = jWtService.createJwtToken(user.getUsername(), refreshExpirySeconds*1000, user.getUserRole().toString());

		RefreshToken refreshToken = RefreshToken.builder().token(jwtToken)
				.expiration(LocalDateTime.now()
						.plusSeconds(refreshExpirySeconds))
				.user(user)
				.build();	 
		refreshTokenRepository.save(refreshToken);


		headers.add(HttpHeaders.SET_COOKIE, generateCookie("rt", jwtToken, refreshExpirySeconds ));
	}



	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> refreshLogin(String refreshtoken) {
		if(refreshtoken==null|| refreshtoken.isEmpty()) {
			throw new InvalidTokenException("Invalid token");	
		}
		
		 String username = jWtService.extractUsername(refreshtoken);
		 String userRole = jWtService.extractUserRole(refreshtoken);
		 
		 return userRepository.findByUsername(username).map(user->{
			 
			 HttpHeaders headers = new HttpHeaders();
				grantAccessToken(headers, user);
				
				System.out.println(user);
				
				return ResponseEntity.ok().headers(headers).body(new ResponseStructure<AuthResponse>()
						.setStatus(HttpStatus.OK.value())
						.setMessage(" refresh login  successfully")
						.setData(AuthResponse.builder().userId(user.getUserId())
								.username(user.getUsername())
								.accssExpiration(accessExpirySeconds)
								.refreshExpiration(refreshExpirySeconds - (LocalDateTime.now().getSecond())).role(user.getUserRole().toString()).build()));

			}).orElseThrow(()-> new UserNotFoundException(" User name is not found"));
	}













}
