package com.javacart.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * OrderItem represents a single line item in an order.
 * This is a snapshot of the product at the time of purchase.
 * 
 * Relationships:
 * - Many OrderItems belong to one Order (N:1)
 * - Many OrderItems reference one Product (N:1)
 * 
 * Why we store priceAtPurchase:
 * - Product prices can change over time
 * - Historical orders must show the price customer actually paid
 * - This creates an immutable record of the transaction
 * 
 * Example: If a laptop costs $1000 today and customer buys it,
 * even if we raise the price to $1200 tomorrow, their order
 * history will still show they paid $1000.
 * 
 * Cascade vs No Cascade on Product:
 * - We DON'T cascade delete from Product to OrderItem
 * - If a product is deleted from catalog, orders remain intact
 * - This preserves historical purchase records
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The order this item belongs to.
     * FetchType.LAZY - Order is loaded only when needed
     * @JoinColumn references the order_id foreign key
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    /**
     * The product that was purchased.
     * We keep a reference to Product so we can display product name/image
     * even after purchase. However, if the product is deleted from catalog,
     * we still have priceAtPurchase for historical accuracy.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    /**
     * Quantity purchased.
     */
    @Column(nullable = false)
    private Integer quantity;
    
    /**
     * The price of the product at the time of purchase.
     * This is copied from Product.price during checkout.
     * Even if Product.price changes later, this value remains unchanged.
     * This ensures order history is accurate and immutable.
     */
    @Column(name = "price_at_purchase", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtPurchase;
    
    /**
     * Calculate the subtotal for this order item.
     * @return priceAtPurchase × quantity
     */
    public BigDecimal getSubtotal() {
        if (priceAtPurchase == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return priceAtPurchase.multiply(new BigDecimal(quantity));
    }
}
