package com.javacart.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity represents a completed purchase.
 * 
 * Relationships:
 * - Many Orders belong to one User (N:1)
 * - One Order has many OrderItems (1:N)
 * 
 * Order Lifecycle:
 * 1. Created during checkout (status = PENDING)
 * 2. Payment processed (status = PAID) - not implemented in this simple app
 * 3. Shipped (status = SHIPPED)
 * 4. Delivered (status = DELIVERED)
 * 
 * Why Orders are immutable:
 * - Order captures a snapshot of the purchase at checkout time
 * - Even if product prices change later, order shows original prices
 * - This is why we store totalPrice and OrderItems have priceAtPurchase
 * 
 * Cascade Strategy:
 * - OrderItems use CascadeType.ALL because they're owned by Order
 * - When Order is deleted, all OrderItems are deleted too
 * - orphanRemoval=true removes OrderItems if removed from the list
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The user who placed this order.
     * FetchType.LAZY - Don't load user unless explicitly needed
     * (Improves performance when listing orders)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * Total price of all items in the order.
     * Calculated during checkout and stored for historical accuracy.
     * Even if product prices change later, this remains unchanged.
     */
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
    
    /**
     * Timestamp when the order was placed.
     */
    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate;
    
    /**
     * Order status: PENDING, PAID, SHIPPED, DELIVERED, CANCELLED
     * Tracks order lifecycle and helps with filtering/reporting.
     */
    @Column(nullable = false, length = 50)
    private String status = "PENDING";
    
    /**
     * Line items in this order (products purchased with quantities and prices).
     * @OneToMany - One order has many order items
     * mappedBy - The field in OrderItem that owns this relationship
     * cascade - When order is saved/deleted, order items are too
     * orphanRemoval - Remove order items if removed from this list
     * FetchType.LAZY - Load order items only when accessed (better performance)
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    /**
     * Set order date automatically before first save.
     */
    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }
    
    /**
     * Helper method to add an order item to this order.
     * Maintains bidirectional relationship consistency.
     * @param orderItem the item to add
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    
    /**
     * Helper method to remove an order item from this order.
     * Maintains bidirectional relationship consistency.
     * @param orderItem the item to remove
     */
    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }
}
