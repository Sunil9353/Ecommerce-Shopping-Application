package com.ecommerce.shopping.requestdto;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
	
	@NotEmpty(message = "")
	private String email;
	private String password;
	
	
	
	
	
	

}
