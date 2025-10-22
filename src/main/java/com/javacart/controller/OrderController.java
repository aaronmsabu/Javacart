package com.javacart.controller;

import com.javacart.entity.Order;
import com.javacart.entity.User;
import com.javacart.service.CartService;
import com.javacart.service.OrderService;
import com.javacart.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * OrderController handles checkout and order history.
 * 
 * Endpoints:
 * - GET /checkout - Display checkout confirmation page
 * - POST /checkout - Process checkout (create order)
 * - GET /orders - Display order history
 * - GET /orders/{id} - Display single order details
 * 
 * Authentication Required:
 * - All endpoints require login
 * - User can only view their own orders
 * 
 * Checkout Flow:
 * 1. User views cart and clicks "Checkout"
 * 2. GET /checkout shows order summary
 * 3. User confirms and submits form
 * 4. POST /checkout processes order:
 *    a. Validates cart not empty
 *    b. Validates stock availability
 *    c. Creates order
 *    d. Decrements stock
 *    e. Clears cart
 * 5. Redirect to order confirmation page
 */
@Controller
public class OrderController {
    
    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;
    
    /**
     * Constructor injection of dependencies.
     */
    public OrderController(OrderService orderService, 
                          CartService cartService, 
                          UserService userService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.userService = userService;
    }
    
    /**
     * Display checkout page.
     * 
     * Shows order summary before final confirmation:
     * - Cart items with quantities and prices
     * - Total amount
     * - Confirm order button
     * 
     * Validation:
     * - If cart is empty, redirect to cart page
     * - If stock insufficient, redirect to cart with error
     * 
     * @param authentication current user
     * @param model Spring MVC model
     * @return template name "checkout"
     */
    @GetMapping("/checkout")
    public String showCheckoutPage(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/login";
        }
        
        // Check if cart is empty
        var cartItems = cartService.getCartItems(user);
        if (cartItems.isEmpty()) {
            return "redirect:/cart?error=empty";
        }
        
        // Validate stock availability
        if (!cartService.validateCartStock(user)) {
            return "redirect:/cart?error=stock";
        }
        
        // Calculate total
        var cartTotal = cartService.calculateCartTotal(user);
        
        // Pass data to view
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);
        
        return "checkout"; // Renders src/main/resources/templates/checkout.html
    }
    
    /**
     * Process checkout (create order).
     * 
     * This is the CRITICAL operation that:
     * 1. Creates order record
     * 2. Copies cart items to order items
     * 3. Decrements product stock
     * 4. Clears user's cart
     * 
     * All steps are transactional in OrderService.checkout().
     * 
     * Success: Redirect to order confirmation page
     * Failure: Redirect to cart with error message
     * 
     * @param authentication current user
     * @return redirect URL
     */
    @PostMapping("/checkout")
    public String processCheckout(Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            // Process checkout (creates order, updates stock, clears cart)
            Order order = orderService.checkout(user);
            
            // Success - redirect to order confirmation
            return "redirect:/orders/" + order.getId() + "?success";
            
        } catch (IllegalStateException e) {
            // Checkout failed (empty cart or insufficient stock)
            return "redirect:/cart?error=checkout&message=" + e.getMessage();
        }
    }
    
    /**
     * Display order history page.
     * 
     * Shows list of all orders placed by the user:
     * - Order ID, date, total amount, status
     * - Link to view order details
     * 
     * @param authentication current user
     * @param model Spring MVC model
     * @return template name "orders"
     */
    @GetMapping("/orders")
    public String orderHistory(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/login";
        }
        
        // Get all orders for this user
        List<Order> orders = orderService.getUserOrders(user);
        
        model.addAttribute("orders", orders);
        model.addAttribute("noOrders", orders.isEmpty());
        
        return "orders"; // Renders src/main/resources/templates/orders.html
    }
    
    /**
     * Display order details page (order confirmation).
     * 
     * Shows detailed information about a single order:
     * - Order ID, date, status
     * - List of items purchased (product, quantity, price)
     * - Total amount
     * 
     * Security:
     * - User can only view their own orders
     * - If trying to access another user's order, redirect with error
     * 
     * @param id order ID from URL
     * @param authentication current user
     * @param model Spring MVC model
     * @return template name "order-detail"
     */
    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable("id") Long id, 
                             Authentication authentication, 
                             Model model) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/login";
        }
        
        // Get order with items
        Order order = orderService.getOrderWithItems(id);
        
        if (order == null) {
            // Order not found
            return "redirect:/orders?error=notfound";
        }
        
        // Security check: user can only view their own orders
        if (!order.getUser().getId().equals(user.getId())) {
            return "redirect:/orders?error=unauthorized";
        }
        
        model.addAttribute("order", order);
        
        return "order-detail"; // Renders src/main/resources/templates/order-detail.html
    }
    
    /**
     * Helper method to get current logged-in user.
     * 
     * @param authentication Spring Security authentication
     * @return User entity or null
     */
    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        return userService.findByUsername(username).orElse(null);
    }
}
