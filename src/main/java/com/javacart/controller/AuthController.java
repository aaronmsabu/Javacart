package com.javacart.controller;

import com.javacart.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * AuthController handles user authentication and registration.
 * 
 * Endpoints:
 * - GET /login - Display login form
 * - GET /register - Display registration form
 * - POST /register - Process registration
 * 
 * Note: POST /login is handled automatically by Spring Security
 * (configured in SecurityConfig). We don't need a controller method for it.
 * 
 * @Controller vs @RestController:
 * - @Controller returns view names (Thymeleaf templates)
 * - @RestController returns JSON/XML (REST APIs)
 * - We use @Controller because we're rendering HTML pages
 */
@Controller
public class AuthController {
    
    private final UserService userService;
    
    /**
     * Constructor injection of UserService.
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Display login page.
     * 
     * Spring Security handles the actual login POST request.
     * We just need to show the login form.
     * 
     * Query Parameters:
     * - error: Present if login failed (wrong username/password)
     * - logout: Present if user just logged out
     * 
     * @param error login error flag
     * @param logout logout success flag
     * @param model Spring MVC model for passing data to view
     * @return template name "login" (renders login.html)
     */
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        return "login"; // Renders src/main/resources/templates/login.html
    }
    
    /**
     * Display registration page.
     * 
     * @return template name "register" (renders register.html)
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // Renders src/main/resources/templates/register.html
    }
    
    /**
     * Process user registration.
     * 
     * Flow:
     * 1. User submits registration form with username, email, password
     * 2. Validate fields are not empty
     * 3. Check if username or email already exists
     * 4. Hash password with BCrypt
     * 5. Save user to database
     * 6. Redirect to login page
     * 
     * Error Handling:
     * - If validation fails, redirect back to register with error message
     * - If registration succeeds, redirect to login with success message
     * 
     * @param username submitted username
     * @param email submitted email
     * @param password submitted password (plain text - will be hashed)
     * @param confirmPassword password confirmation
     * @param model Spring MVC model for passing error messages to view
     * @return redirect URL or template name
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam("username") String username,
                              @RequestParam("email") String email,
                              @RequestParam("password") String password,
                              @RequestParam("confirmPassword") String confirmPassword,
                              Model model) {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("error", "Username is required");
            return "register";
        }
        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("error", "Email is required");
            return "register";
        }
        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Password is required");
            return "register";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }
        if (password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters");
            return "register";
        }
        
        try {
            // Register user (password will be hashed in UserService)
            userService.registerUser(username.trim(), email.trim(), password);
            
            // Redirect to login with success message
            return "redirect:/login?registered";
            
        } catch (IllegalArgumentException e) {
            // Registration failed (username or email already exists)
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
