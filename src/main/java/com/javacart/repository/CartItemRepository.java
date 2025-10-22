package com.javacart.repository;

import com.javacart.entity.CartItem;
import com.javacart.entity.User;
import com.javacart.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CartItemRepository manages shopping cart data in the database.
 * 
 * Cart Persistence Strategy:
 * - Cart items are stored in the database (not session) for persistence
 * - User can close browser and come back - cart items remain
 * - Each user can have multiple cart items (one per product)
 * - Unique constraint ensures one CartItem per (user, product) pair
 * 
 * Why database-backed cart instead of session-based:
 * - Survives browser restarts and session timeouts
 * - Can be synced across multiple devices
 * - Easier to implement cart recovery and analytics
 * - Simpler than Redis/Memcached for small-scale apps
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    /**
     * Find all cart items for a specific user.
     * Used to display the shopping cart page.
     * 
     * @param user the user whose cart to retrieve
     * @return list of cart items (empty if cart is empty)
     */
    List<CartItem> findByUser(User user);
    
    /**
     * Find a specific cart item by user and product.
     * Used to check if product is already in cart before adding.
     * 
     * Why Optional?
     * - Product might not be in cart yet (first time adding)
     * - Optional prevents NullPointerException and makes intent clear
     * 
     * @param user the user
     * @param product the product
     * @return Optional containing CartItem if found, empty otherwise
     */
    Optional<CartItem> findByUserAndProduct(User user, Product product);
    
    /**
     * Delete all cart items for a user.
     * Called after successful checkout to clear the cart.
     * 
     * Why void? Spring Data JPA handles the delete operation automatically.
     * No need to return deleted items or count.
     * 
     * @param user the user whose cart to clear
     */
    void deleteByUser(User user);
    
    /**
     * Count the number of items in a user's cart.
     * Used to display cart badge count (e.g., "Cart (3)" in navbar).
     * 
     * @param user the user
     * @return number of distinct products in cart (not total quantity)
     */
    long countByUser(User user);
    
    /**
     * Calculate the total value of all items in a user's cart.
     * Uses custom JPQL query with SUM aggregate function.
     * 
     * Why custom query?
     * - We need to multiply price × quantity for each item and sum them
     * - Can't express this with derived query method name
     * 
     * COALESCE(SUM(...), 0) ensures we return 0 instead of NULL for empty cart.
     * 
     * @param userId the user's ID
     * @return total cart value (sum of all item subtotals)
     */
    @Query("SELECT COALESCE(SUM(c.quantity * c.product.price), 0) " +
           "FROM CartItem c WHERE c.user.id = :userId")
    java.math.BigDecimal calculateCartTotal(@Param("userId") Long userId);
}
