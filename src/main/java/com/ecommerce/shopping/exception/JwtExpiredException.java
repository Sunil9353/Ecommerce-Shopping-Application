package com.ecommerce.shopping.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("serial")
@Getter
@AllArgsConstructor
public class JwtExpiredException extends RuntimeException {
  private String message;
  
}
