package com.javacart.controller;

import com.javacart.entity.CartItem;
import com.javacart.entity.User;
import com.javacart.service.CartService;
import com.javacart.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * CartController manages shopping cart operations.
 * 
 * Endpoints:
 * - GET /cart - Display cart page
 * - POST /cart/add - Add product to cart
 * - POST /cart/update - Update item quantity
 * - POST /cart/remove - Remove item from cart
 * 
 * Authentication Required:
 * - All endpoints require user to be logged in
 * - Spring Security automatically redirects to /login if not authenticated
 * - User information retrieved from Authentication object
 * 
 * How Authentication Works:
 * - After login, Spring Security creates an Authentication object
 * - Authentication.getName() returns the username
 * - We use username to fetch User entity from database
 * - User entity is needed to query/modify cart
 */
@Controller
public class CartController {
    
    private final CartService cartService;
    private final UserService userService;
    
    /**
     * Constructor injection of dependencies.
     */
    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }
    
    /**
     * Display shopping cart page.
     * 
     * Shows:
     * - List of items in cart (product, quantity, subtotal)
     * - Total cart value
     * - Links to update quantity or remove items
     * - Checkout button
     * 
     * @param authentication Spring Security authentication object (injected automatically)
     * @param model Spring MVC model
     * @return template name "cart"
     */
    @GetMapping("/cart")
    public String viewCart(Authentication authentication, Model model) {
        // Get current logged-in user
        User user = getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/login"; // Should never happen (Security prevents access)
        }
        
        // Get cart items and total
        List<CartItem> cartItems = cartService.getCartItems(user);
        BigDecimal cartTotal = cartService.calculateCartTotal(user);
        
        // Add data to model for Thymeleaf template
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);
        model.addAttribute("cartEmpty", cartItems.isEmpty());
        
        return "cart"; // Renders src/main/resources/templates/cart.html
    }
    
    /**
     * Add product to cart.
     * 
     * Form Parameters:
     * - productId: ID of product to add
     * - quantity: Quantity to add (default 1)
     * 
     * Process:
     * 1. Get current user
     * 2. Call CartService.addToCart()
     * 3. Redirect back to product page or cart
     * 
     * Error Handling:
     * - If product not found, redirect with error
     * - If quantity invalid, redirect with error
     * 
     * @param productId product to add
     * @param quantity quantity to add
     * @param authentication current user's authentication
     * @return redirect URL
     */
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId,
                           @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                           Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            cartService.addToCart(user, productId, quantity);
            return "redirect:/cart?added"; // Success - redirect to cart
        } catch (IllegalArgumentException e) {
            // Product not found or invalid quantity
            return "redirect:/products?error=" + e.getMessage();
        }
    }
    
    /**
     * Update cart item quantity.
     * 
     * Form Parameters:
     * - cartItemId: ID of cart item to update
     * - quantity: New quantity (if 0 or negative, item is removed)
     * 
     * @param cartItemId cart item to update
     * @param quantity new quantity
     * @param authentication current user's authentication
     * @return redirect URL
     */
    @PostMapping("/cart/update")
    public String updateCartItem(@RequestParam("cartItemId") Long cartItemId,
                                @RequestParam("quantity") int quantity,
                                Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            cartService.updateQuantity(cartItemId, quantity);
            return "redirect:/cart?updated";
        } catch (IllegalArgumentException e) {
            return "redirect:/cart?error=" + e.getMessage();
        }
    }
    
    /**
     * Remove item from cart.
     * 
     * Form Parameter:
     * - cartItemId: ID of cart item to remove
     * 
     * @param cartItemId cart item to remove
     * @param authentication current user's authentication
     * @return redirect URL
     */
    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam("cartItemId") Long cartItemId,
                                 Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            cartService.removeFromCart(cartItemId);
            return "redirect:/cart?removed";
        } catch (IllegalArgumentException e) {
            return "redirect:/cart?error=" + e.getMessage();
        }
    }
    
    /**
     * Helper method to get current logged-in user.
     * 
     * Spring Security provides Authentication object with username.
     * We use username to fetch User entity from database.
     * 
     * @param authentication Spring Security authentication
     * @return User entity or null if not found
     */
    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        return userService.findByUsername(username).orElse(null);
    }
}
