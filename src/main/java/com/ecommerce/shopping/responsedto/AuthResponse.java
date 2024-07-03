package com.ecommerce.shopping.responsedto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AuthResponse {
	private int userId;
	private String username;
	private long accssExpiration;
	private long refreshExpiration;
	private String role;
	

}
