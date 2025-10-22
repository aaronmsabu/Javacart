package com.javacart.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Product entity represents items available for purchase in the store.
 * 
 * Relationships:
 * - One Product can appear in many CartItems (1:N)
 * - One Product can appear in many OrderItems (1:N)
 * 
 * Business Logic:
 * - Stock must be checked before adding to cart or completing checkout
 * - Stock is decremented during checkout (not when adding to cart)
 * - Price uses BigDecimal for precise monetary calculations (avoids floating-point errors)
 * 
 * @Entity - JPA entity annotation
 * @Table - Maps to "products" table
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    /**
     * Primary key with auto-increment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Product name displayed to customers.
     * Indexed for faster search queries.
     */
    @Column(nullable = false, length = 200)
    private String name;
    
    /**
     * Detailed product description.
     * TEXT column type allows longer content (up to 65,535 characters in MySQL).
     */
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * Product price in USD (or your currency).
     * BigDecimal ensures precise decimal arithmetic (no rounding errors).
     * Scale of 2 means two decimal places (cents).
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    /**
     * Available stock quantity.
     * Decremented during checkout, incremented on restocking.
     * Must check stock > 0 before allowing purchase.
     */
    @Column(nullable = false)
    private Integer stock = 0;
    
    /**
     * URL to product image (can be external CDN or local path).
     * Example: "https://example.com/images/product123.jpg"
     */
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    /**
     * Timestamp when product was added to catalog.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Set creation timestamp automatically before first save.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    /**
     * Check if product is in stock.
     * @return true if stock quantity is greater than 0
     */
    public boolean isInStock() {
        return stock != null && stock > 0;
    }
    
    /**
     * Check if sufficient quantity is available for purchase.
     * @param quantity requested quantity
     * @return true if stock is sufficient
     */
    public boolean hasStock(int quantity) {
        return stock != null && stock >= quantity;
    }
}
