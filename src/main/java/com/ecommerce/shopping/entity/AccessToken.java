package com.ecommerce.shopping.entity;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
public class AccessToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int tokenId;
	private String token;
	private LocalDateTime expiration;
	
	@ManyToOne
	private User user;
	

}
