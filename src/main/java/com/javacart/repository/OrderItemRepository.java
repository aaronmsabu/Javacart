package com.javacart.repository;

import com.javacart.entity.OrderItem;
import com.javacart.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * OrderItemRepository manages individual line items within orders.
 * 
 * Usage:
 * - Typically accessed through Order.getOrderItems() (lazy loading)
 * - Can query directly for analytics (e.g., most purchased products)
 * - Not frequently used directly in controllers (Order entity handles most cases)
 * 
 * Why separate repository?
 * - Follows Repository pattern (one repository per entity)
 * - Allows independent queries on order items if needed
 * - Enables future features like "Products you've purchased" recommendations
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * Find all items in a specific order.
     * Usually accessed via order.getOrderItems(), but this provides
     * a direct query option if needed.
     * 
     * @param order the order
     * @return list of order items
     */
    List<OrderItem> findByOrder(Order order);
    
    /**
     * Find all order items for a specific product across all orders.
     * Useful for analytics: "How many times has this product been purchased?"
     * Not used in basic shopping cart, but helpful for future features.
     * 
     * @param productId the product ID
     * @return list of all order items for that product
     */
    List<OrderItem> findByProductId(Long productId);
}
