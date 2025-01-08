package com.techlabs.repository;

import com.techlabs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByMobileNumber(String mobileNumber);

    Optional<User> findByMobileNumber(String mobileNumber);
}
