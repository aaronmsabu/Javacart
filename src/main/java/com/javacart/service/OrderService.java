package com.javacart.service;

import com.javacart.entity.*;
import com.javacart.repository.OrderRepository;
import com.javacart.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * OrderService manages order processing and order history.
 * 
 * Checkout Process (CRITICAL - must be atomic):
 * 1. Validate cart is not empty
 * 2. Validate all items have sufficient stock
 * 3. Create Order entity
 * 4. Copy CartItems to OrderItems (with current prices)
 * 5. Decrement product stock
 * 6. Clear user's cart
 * 7. Save everything to database
 * 
 * Why @Transactional is ESSENTIAL:
 * - All steps above must succeed or fail together (atomicity)
 * - If stock update fails, order shouldn't be created
 * - If cart clear fails, order shouldn't be recorded
 * - Transaction ensures database consistency
 * 
 * Concurrency Considerations:
 * - Multiple users can order the same product simultaneously
 * - Product stock could become negative without proper handling
 * - Solution: Check stock INSIDE transaction before decrementing
 * - Alternative: Use database-level constraints or pessimistic locking
 *   (not implemented here to keep it beginner-friendly)
 */
@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final ProductService productService;
    
    /**
     * Constructor injection of dependencies.
     */
    public OrderService(OrderRepository orderRepository, 
                       OrderItemRepository orderItemRepository,
                       CartService cartService,
                       ProductService productService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
        this.productService = productService;
    }
    
    /**
     * Process checkout: create order from cart items.
     * 
     * This is the MOST CRITICAL method in the entire application.
     * It must be transactional to ensure data consistency.
     * 
     * Process:
     * 1. Get cart items
     * 2. Validate cart not empty
     * 3. Validate stock availability
     * 4. Create Order entity
     * 5. For each cart item:
     *    a. Create OrderItem with current product price
     *    b. Decrement product stock
     * 6. Calculate and set total price
     * 7. Clear cart
     * 8. Save order
     * 
     * @Transactional ensures:
     * - All database operations succeed or all are rolled back
     * - Prevents partial orders (e.g., order created but stock not updated)
     * - Provides isolation from concurrent transactions
     * 
     * @param user the user checking out
     * @return the created Order
     * @throws IllegalStateException if cart is empty or stock insufficient
     */
    @Transactional
    public Order checkout(User user) {
        // Step 1: Get cart items
        List<CartItem> cartItems = cartService.getCartItems(user);
        
        // Step 2: Validate cart not empty
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot checkout with empty cart");
        }
        
        // Step 3: Validate stock availability for ALL items
        // This prevents partial fulfillment (some items in stock, others not)
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (!product.hasStock(cartItem.getQuantity())) {
                throw new IllegalStateException(
                    "Insufficient stock for product: " + product.getName() +
                    ". Available: " + product.getStock() + ", Requested: " + cartItem.getQuantity()
                );
            }
        }
        
        // Step 4: Create Order entity
        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        
        // Step 5: Convert CartItems to OrderItems and update stock
        BigDecimal totalPrice = BigDecimal.ZERO;
        
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            
            // Create OrderItem (snapshot of purchase)
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice()); // Current price at checkout
            
            // Add to order's item list
            order.addOrderItem(orderItem);
            
            // Calculate subtotal
            BigDecimal subtotal = product.getPrice()
                .multiply(new BigDecimal(cartItem.getQuantity()));
            totalPrice = totalPrice.add(subtotal);
            
            // Decrement stock (CRITICAL: must happen inside transaction)
            boolean stockUpdated = productService.updateStock(product.getId(), cartItem.getQuantity());
            if (!stockUpdated) {
                // This should never happen because we validated stock above,
                // but we check again in case of concurrent orders
                throw new IllegalStateException(
                    "Failed to update stock for product: " + product.getName() +
                    ". Possible concurrent order conflict."
                );
            }
        }
        
        // Step 6: Set total price
        order.setTotalPrice(totalPrice);
        
        // Step 7: Save order (cascade saves OrderItems too)
        Order savedOrder = orderRepository.save(order);
        
        // Step 8: Clear cart
        cartService.clearCart(user);
        
        return savedOrder;
    }
    
    /**
     * Get all orders for a user, sorted by date (newest first).
     * Used for order history page.
     * 
     * @param user the user
     * @return list of orders
     */
    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }
    
    /**
     * Find an order by ID.
     * 
     * @param orderId the order ID
     * @return Order if found, null otherwise
     */
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }
    
    /**
     * Get order details with items (for order confirmation/history page).
     * 
     * @param orderId the order ID
     * @return Order with items loaded, or null if not found
     */
    public Order getOrderWithItems(Long orderId) {
        Order order = findById(orderId);
        if (order != null) {
            // Trigger lazy loading of order items
            order.getOrderItems().size();
        }
        return order;
    }
    
    /**
     * Count total orders for a user.
     * 
     * @param user the user
     * @return number of orders
     */
    public long countUserOrders(User user) {
        return orderRepository.countByUser(user);
    }
    
    /**
     * Calculate total amount spent by user across all orders.
     * 
     * @param user the user
     * @return total amount spent
     */
    public BigDecimal calculateTotalSpent(User user) {
        return orderRepository.calculateTotalSpent(user.getId());
    }
}
