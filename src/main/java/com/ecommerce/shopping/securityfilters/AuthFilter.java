package com.ecommerce.shopping.securityfilters;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecommerce.shopping.exception.IllegalArguementException;
import com.ecommerce.shopping.serviceimpl.JWtService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
public class AuthFilter extends OncePerRequestFilter {

	private JWtService jWtService;

	public AuthFilter(JWtService jWtService) {
		super();
		this.jWtService = jWtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token=null;
		if(request.getCookies()!=null) {
			for(Cookie cookie : request.getCookies()) {
				System.out.println("Hii hello ");
				System.out.println(cookie.getName());
				if("at".equals(cookie.getName())) {
					token=cookie.getValue();
					
				}
			}
		}
		System.err.println(token);
		if(token != null) {
			System.err.println("taken is not null");
		try {
			
			if(SecurityContextHolder.getContext().getAuthentication()==null) {
				String username=	jWtService.extractUsername(token);
				String role = jWtService.extractUserRole(token);

				if(username==null||role==null) {
					throw new IllegalArguementException("Username or role  is null");

				}

				Collection<?  extends GrantedAuthority> userRole= Collections.singletonList(new SimpleGrantedAuthority(role));
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, userRole);
				usernamePasswordAuthenticationToken.setDetails( new WebAuthenticationDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
//		catch(ExpiredJwtException ex) {
//			System.err.println(ex.getMessage());
////			TokenExceptionHandler.tokenHandler(HttpStatus.UNAUTHORIZED.value(), "Failed to authenticate", "The token is already expired", response);
//			
//		}
//
		catch(JwtException e) {
			TokenExceptionHandler.tokenHandler(HttpStatus.UNAUTHORIZED.value(), "Failed to Authenticate", "Invalid token", response);

		}
		
	}
		filterChain.doFilter(request, response);

	}
}
