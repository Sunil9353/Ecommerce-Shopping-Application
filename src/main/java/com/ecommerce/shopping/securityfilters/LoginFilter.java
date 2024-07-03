package com.ecommerce.shopping.securityfilters;
import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {


		Cookie[] cookies = request.getCookies();

		if(cookies!=null) {
			for(Cookie cookie:request.getCookies()) {
				if("at".equals(cookie.getName()) || "rt".equals(cookie.getName())) {
					TokenExceptionHandler.tokenHandler(HttpStatus.BAD_REQUEST.value(),"Failed to Login","User is already logged in",response);
				
				}
			}

		}

		filterChain.doFilter(request, response);
	}

}
