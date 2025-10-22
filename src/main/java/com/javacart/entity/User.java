package com.javacart.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User entity represents registered users in the system.
 * 
 * Relationships:
 * - One User can have many CartItems (1:N)
 * - One User can have many Orders (1:N)
 * 
 * Security Note: Password must ALWAYS be hashed using BCrypt before storing.
 * Never store plain text passwords!
 * 
 * @Entity - Marks this class as a JPA entity (maps to database table)
 * @Table - Specifies the table name and adds unique constraints
 * @Data - Lombok generates getters, setters, toString, equals, and hashCode
 * @NoArgsConstructor - Lombok generates no-argument constructor (required by JPA)
 * @AllArgsConstructor - Lombok generates constructor with all fields
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /**
     * Primary key with auto-generated values.
     * @Id marks this field as the primary key
     * @GeneratedValue with IDENTITY strategy uses database auto-increment
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Username for login - must be unique across all users.
     * @Column defines constraints: unique, not null, and length limit
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    /**
     * Email address - must be unique across all users.
     * Used for account recovery and notifications.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    /**
     * BCrypt-hashed password (never store plain text!).
     * BCrypt hashes are typically 60 characters, but we allow 255 for flexibility.
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
    
    /**
     * User role: "USER" or "ADMIN".
     * Determines access permissions (e.g., only admins can manage products).
     * Default value is "USER" for new registrations.
     */
    @Column(nullable = false, length = 20)
    private String role = "USER";
    
    /**
     * Timestamp when the user account was created.
     * @Column with updatable=false ensures this value never changes after creation.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Automatically set creation timestamp before persisting to database.
     * @PrePersist is a JPA callback that runs before INSERT operations.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
