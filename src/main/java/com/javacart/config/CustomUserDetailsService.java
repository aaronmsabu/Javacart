package com.javacart.config;

import com.javacart.entity.User;
import com.javacart.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Custom UserDetailsService implementation for Spring Security.
 * 
 * Spring Security needs to load user information during authentication.
 * This service tells Spring Security how to find users in our database.
 * 
 * How Authentication Works:
 * 1. User submits username and password via login form
 * 2. Spring Security calls loadUserByUsername(username)
 * 3. We query database for user
 * 4. We convert our User entity to Spring Security's UserDetails
 * 5. Spring Security compares submitted password with stored hash
 * 6. If match, user is authenticated and session is created
 * 
 * Why UserDetails?
 * - Spring Security's standard interface for user information
 * - Contains username, password, authorities (roles), account status
 * - We adapt our User entity to this interface
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    /**
     * Constructor injection of UserRepository.
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Load user by username for authentication.
     * 
     * Called by Spring Security during login.
     * 
     * Process:
     * 1. Query database for user by username
     * 2. If not found, throw UsernameNotFoundException
     * 3. Convert User entity to Spring Security UserDetails
     * 4. Return UserDetails with hashed password and authorities
     * 
     * Spring Security then compares the submitted password (from login form)
     * with the hashed password (from UserDetails) using BCrypt.
     * 
     * @param username the username submitted in login form
     * @return UserDetails containing user information
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Query database for user
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        // Convert our User entity to Spring Security's UserDetails
        // Spring Security's User class (not our User entity!)
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPasswordHash())  // Already BCrypt hashed
            .authorities(Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole()) // ROLE_USER or ROLE_ADMIN
            ))
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build();
    }
}
