package com.javacart.service;

import com.javacart.entity.User;
import com.javacart.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * UserService handles business logic for user management.
 * 
 * Responsibilities:
 * - User registration with password hashing
 * - User lookup for authentication
 * - User validation (unique username/email)
 * 
 * Service Layer Pattern:
 * - Separates business logic from controllers
 * - Controllers handle HTTP requests/responses
 * - Services handle business rules and validation
 * - Repositories handle database access
 * 
 * Why @Service?
 * - Marks this class as a Spring-managed service component
 * - Spring creates a singleton instance at startup
 * - Enables dependency injection into controllers
 * 
 * Constructor Injection:
 * - Dependencies injected via constructor (not @Autowired on fields)
 * - Makes dependencies explicit and enables easier testing
 * - Recommended best practice in Spring Boot
 */
@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Constructor injection of dependencies.
     * Spring automatically provides these beans at runtime.
     * 
     * @param userRepository repository for database access
     * @param passwordEncoder BCrypt encoder for password hashing
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Register a new user with encrypted password.
     * 
     * Process:
     * 1. Validate username and email are unique
     * 2. Hash the password using BCrypt
     * 3. Set default role to "USER"
     * 4. Save to database
     * 
     * Why BCrypt?
     * - Industry standard for password hashing
     * - Automatically salts passwords (prevents rainbow table attacks)
     * - Slow by design (makes brute force attacks impractical)
     * 
     * @param username unique username
     * @param email unique email
     * @param rawPassword plain text password (will be hashed)
     * @return the saved User entity
     * @throws IllegalArgumentException if username or email already exists
     */
    public User registerUser(String username, String email, String rawPassword) {
        // Validate uniqueness
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Create new user with hashed password
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword)); // Hash password with BCrypt
        user.setRole("USER"); // Default role for new users
        
        return userRepository.save(user);
    }
    
    /**
     * Find a user by username.
     * Used during authentication and profile lookup.
     * 
     * @param username the username to search for
     * @return Optional containing User if found
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Find a user by email.
     * Used for password reset and duplicate check during registration.
     * 
     * @param email the email to search for
     * @return Optional containing User if found
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Find a user by ID.
     * 
     * @param id the user ID
     * @return Optional containing User if found
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Authenticate user credentials.
     * Verifies that the provided password matches the stored hash.
     * 
     * @param username the username
     * @param rawPassword the plain text password to verify
     * @return true if credentials are valid, false otherwise
     */
    public boolean authenticate(String username, String rawPassword) {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        // Use BCrypt to compare raw password with stored hash
        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }
}
