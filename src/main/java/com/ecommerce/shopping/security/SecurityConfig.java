package com.ecommerce.shopping.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecommerce.shopping.securityfilters.AuthFilter;
import com.ecommerce.shopping.securityfilters.LoginFilter;
import com.ecommerce.shopping.serviceimpl.JWtService;

import lombok.AllArgsConstructor;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

	private JWtService jWtService;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}
	
	
	@Order(1)
	@Bean
	SecurityFilterChain loginSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.csrf(csrf -> csrf.disable())
				.securityMatchers(match -> match.requestMatchers("/api/version1/login/**","/api/version1/sellers/register","/api/version1/customers/register"))
				.authorizeHttpRequests(authories -> authories.anyRequest().permitAll())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(new LoginFilter(), UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Order(2)
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.csrf(csrf -> csrf.disable())
				.securityMatchers(match -> match.requestMatchers("/api/version1/**"))
				.authorizeHttpRequests(authories -> authories.anyRequest().permitAll())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(new AuthFilter(jWtService), UsernamePasswordAuthenticationFilter.class)
				.build();
	}
	
	@Order(3)
	@Bean
	SecurityFilterChain refreshFilterChain(HttpSecurity httpSecurity) throws Exception {	
	return 	httpSecurity.csrf(csrf->csrf.disable())
			.securityMatchers(match->match.requestMatchers("/api/version1/refresh/**"))
		    .authorizeHttpRequests(authorize->authorize.anyRequest().authenticated())
		    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))	
		    .addFilterBefore(new AuthFilter(jWtService), UsernamePasswordAuthenticationFilter.class)
		    .build();
	}
	

	@Bean
	AuthenticationManager getAuthenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();

	}

}








































