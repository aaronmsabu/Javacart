package com.javacart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application.
 * 
 * Spring Security provides:
 * - Authentication (who are you?)
 * - Authorization (what can you do?)
 * - Protection against common attacks (CSRF, XSS, etc.)
 * 
 * Configuration Strategy:
 * - Form-based login (session-based, not token-based)
 * - Public access to product pages (anyone can browse)
 * - Protected access to cart and checkout (must be logged in)
 * - BCrypt password encoding
 * 
 * Why @Configuration and @EnableWebSecurity?
 * - @Configuration tells Spring this class defines beans
 * - @EnableWebSecurity enables Spring Security for the application
 * - Methods annotated with @Bean create Spring-managed objects
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * Configure security filter chain.
     * 
     * This defines what URLs are public vs protected.
     * 
     * URL Authorization Rules:
     * - / (home/products) - public
     * - /products/** - public
     * - /register, /login - public
     * - /css/**, /js/**, /images/** - public (static resources)
     * - /cart/**, /checkout/**, /orders/** - require authentication
     * 
     * Login Configuration:
     * - Login page: /login
     * - Success redirect: / (home page)
     * - Logout: /logout redirects to /login
     * 
     * CSRF Protection:
     * - Enabled by default (prevents cross-site request forgery)
     * - Forms must include CSRF token (Thymeleaf handles this automatically)
     * 
     * @param http HttpSecurity builder
     * @return configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (no login required)
                .requestMatchers("/", "/products", "/products/**", "/register", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                // Protected endpoints (login required)
                .requestMatchers("/cart/**", "/checkout/**", "/orders/**").authenticated()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            // Form-based login configuration
            .formLogin(form -> form
                .loginPage("/login")                    // Custom login page
                .defaultSuccessUrl("/", true)           // Redirect after successful login
                .permitAll()                             // Everyone can access login page
            )
            // Logout configuration
            .logout(logout -> logout
                .logoutUrl("/logout")                    // Logout endpoint
                .logoutSuccessUrl("/login?logout")       // Redirect after logout
                .invalidateHttpSession(true)             // Clear session
                .deleteCookies("JSESSIONID")             // Remove session cookie
                .permitAll()
            )
            // Exception handling (redirect to login for unauthenticated users)
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/login?error")        // Redirect if access denied
            );
        
        return http.build();
    }
    
    /**
     * Password encoder bean.
     * 
     * BCrypt is a secure password hashing function:
     * - Automatically generates salt (prevents rainbow table attacks)
     * - Slow by design (makes brute force attacks impractical)
     * - Industry standard for password storage
     * 
     * Strength 10 is the default and provides good balance between
     * security and performance. Higher values are slower but more secure.
     * 
     * NEVER store passwords in plain text!
     * NEVER use MD5 or SHA1 for passwords (they're too fast and crackable)!
     * 
     * @return BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10); // Strength 10 (default)
    }
}
