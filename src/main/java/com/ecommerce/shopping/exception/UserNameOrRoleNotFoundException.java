package com.ecommerce.shopping.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("serial")
@AllArgsConstructor
@Getter
public class UserNameOrRoleNotFoundException extends RuntimeException {
	String message;

}
