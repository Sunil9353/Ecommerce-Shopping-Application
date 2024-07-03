package com.ecommerce.shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.shopping.entity.AccessToken;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Integer> {

}
