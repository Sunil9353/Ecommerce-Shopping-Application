package com.ecommerce.shopping.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ecommerce.shopping.entity.User;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	boolean  existsByEmail(String email);

	Optional<User> findByUsername(String username);

}
