package com.ecommerce.shopping.entity;
import java.sql.Ref;

import com.ecommerce.shopping.enums.UserRole;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertFalse.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
@Setter
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	private String username;
	private String email;
	private String password;
	private UserRole userRole;
	private boolean isEmialVerified;
	private boolean isDeleted;
	
//	@OneToMany
//	private java.util.List<AccessToken> accessTokens;
	
//	@OneToMany
//	private java.util.List<RefreshToken> refreshTokens;
	
	
	
	@Override
	public String toString() {
		return "User [userId=" + userId + ", username=" + username + ", email=" + email + ", password=" + password
				+ ", userRole=" + userRole + ", isEmialVerified=" + isEmialVerified + ", isDeleted=" + isDeleted + "]";
	}
	

}
