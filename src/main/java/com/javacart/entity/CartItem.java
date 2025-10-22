package com.javacart.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CartItem represents a single product in a user's shopping cart.
 * 
 * Relationships:
 * - Many CartItems belong to one User (N:1)
 * - Many CartItems reference one Product (N:1)
 * 
 * Business Logic:
 * - Each user can have only ONE cart item per product (enforced by unique constraint)
 * - Quantity can be updated (increase/decrease)
 * - Cart items are temporary - they're cleared after successful checkout
 * - If user deletes their account, cart items are also deleted (CASCADE)
 * 
 * Fetch Strategy:
 * - User and Product use EAGER fetch because we almost always need these details
 *   when displaying the cart (to show product names, prices, etc.)
 * - Alternative: Use LAZY + explicit JOIN FETCH in queries for better performance
 */
@Entity
@Table(name = "cart_items", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The user who owns this cart item.
     * @ManyToOne - Many cart items can belong to one user
     * @JoinColumn - Foreign key column name in database
     * FetchType.EAGER - Load user data immediately (default for ManyToOne)
     * 
     * Note: EAGER fetch can cause performance issues with large datasets.
     * Consider LAZY + JOIN FETCH in queries for production optimization.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * The product being added to the cart.
     * @ManyToOne - Many cart items can reference one product
     * FetchType.EAGER - We need product details (name, price, image) to display cart
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    /**
     * Quantity of this product in the cart.
     * Must be at least 1 (validated in service layer).
     * Must not exceed available stock (checked during checkout).
     */
    @Column(nullable = false)
    private Integer quantity = 1;
    
    /**
     * Calculate the subtotal for this cart item (price × quantity).
     * This is a derived value, not stored in database.
     * @return subtotal amount
     */
    public java.math.BigDecimal getSubtotal() {
        if (product == null || product.getPrice() == null || quantity == null) {
            return java.math.BigDecimal.ZERO;
        }
        return product.getPrice().multiply(new java.math.BigDecimal(quantity));
    }
}
