package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    // Find a user by their email (used for login and registration)
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    // Find a user by their token (used for password reset or OTP verification)
    Optional<User> findByToken(String token);
    
    // Find users by their role (Donor, Receiver, Admin)
    List<User> findByRole(String role);
    
   
}
