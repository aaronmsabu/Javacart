package com.javacart.repository;

import com.javacart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository provides database access for User entities.
 * 
 * Spring Data JPA automatically implements this interface at runtime.
 * No need to write SQL queries - Spring generates them based on method names!
 * 
 * JpaRepository<User, Long> provides:
 * - save(User) - Insert or update
 * - findById(Long) - Find by primary key
 * - findAll() - Get all users
 * - deleteById(Long) - Delete by primary key
 * - count() - Count total users
 * 
 * Derived Query Methods:
 * Spring Data JPA parses method names and generates SQL automatically.
 * Format: findBy[Property][Operation]
 * Examples:
 * - findByUsername → SELECT * FROM users WHERE username = ?
 * - findByEmail → SELECT * FROM users WHERE email = ?
 * - existsByUsername → SELECT COUNT(*) FROM users WHERE username = ?
 * 
 * @Repository marks this as a data access component (optional with Spring Data JPA,
 * but included for clarity and consistency).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find a user by username (for login authentication).
     * Returns Optional to safely handle case when user doesn't exist.
     * 
     * @param username the username to search for
     * @return Optional containing User if found, empty Optional otherwise
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find a user by email address (for registration validation and password reset).
     * 
     * @param email the email to search for
     * @return Optional containing User if found, empty Optional otherwise
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if a username already exists (for registration validation).
     * More efficient than findByUsername when you only need existence check.
     * 
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if an email already exists (for registration validation).
     * 
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
