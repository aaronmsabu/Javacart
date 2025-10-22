package com.javacart.repository;

import com.javacart.entity.Order;
import com.javacart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OrderRepository manages completed orders in the database.
 * 
 * Order History and Reporting:
 * - Users can view their past orders (order history page)
 * - Admins can view all orders (order management dashboard - not implemented)
 * - Orders are sorted by date (most recent first)
 * - Can filter by status, date range, etc.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Find all orders for a specific user, sorted by date (newest first).
     * Used to display order history page.
     * 
     * @param user the user whose orders to retrieve
     * @return list of orders sorted by order date descending
     */
    List<Order> findByUserOrderByOrderDateDesc(User user);
    
    /**
     * Find orders by user and status.
     * Example: Find all "PENDING" orders for a user.
     * Useful for filtering order history or admin dashboards.
     * 
     * @param user the user
     * @param status the order status (PENDING, PAID, SHIPPED, etc.)
     * @return list of matching orders
     */
    List<Order> findByUserAndStatus(User user, String status);
    
    /**
     * Find orders placed within a date range.
     * Useful for reporting and analytics (not used in this simple app).
     * 
     * @param startDate start of date range
     * @param endDate end of date range
     * @return list of orders in date range
     */
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Count total orders for a user.
     * Can be used to show "You have placed 5 orders" on profile page.
     * 
     * @param user the user
     * @return number of orders
     */
    long countByUser(User user);
    
    /**
     * Calculate total amount spent by a user across all orders.
     * Custom JPQL query with SUM aggregate.
     * 
     * @param userId the user's ID
     * @return total amount spent (sum of all order totals)
     */
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) " +
           "FROM Order o WHERE o.user.id = :userId")
    java.math.BigDecimal calculateTotalSpent(@Param("userId") Long userId);
}
