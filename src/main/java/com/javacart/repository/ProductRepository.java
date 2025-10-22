package com.javacart.repository;

import com.javacart.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ProductRepository provides database access for Product entities.
 * 
 * In addition to basic CRUD operations from JpaRepository,
 * this interface defines custom search queries for product catalog features.
 * 
 * Query Methods vs @Query:
 * - Simple queries use derived method names (findByName)
 * - Complex queries use @Query annotation with JPQL (Java Persistence Query Language)
 * - JPQL is similar to SQL but works with entity classes instead of table names
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Search products by name (case-insensitive partial match).
     * Example: "laptop" will match "Dell Laptop", "Gaming Laptop", etc.
     * 
     * Derived query method - Spring generates:
     * SELECT * FROM products WHERE LOWER(name) LIKE LOWER('%keyword%')
     * 
     * @param name the search keyword
     * @return list of matching products
     */
    List<Product> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find all products ordered by name alphabetically.
     * Useful for displaying sorted product catalog.
     * 
     * @return list of all products sorted by name
     */
    List<Product> findAllByOrderByNameAsc();
    
    /**
     * Find products with stock greater than zero (available for purchase).
     * Useful for filtering out of stock items.
     * 
     * @return list of in-stock products
     */
    List<Product> findByStockGreaterThan(Integer stock);
    
    /**
     * Search products by name or description (advanced search).
     * Uses custom JPQL query for more flexibility.
     * 
     * @Query annotation defines explicit JPQL (not derived from method name)
     * :keyword is a named parameter (prevents SQL injection)
     * LOWER() makes search case-insensitive
     * 
     * @param keyword the search term to match
     * @return list of matching products
     */
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);
    
    /**
     * Find products within a price range.
     * Useful for filtering by price in product catalog.
     * 
     * @param minPrice minimum price (inclusive)
     * @param maxPrice maximum price (inclusive)
     * @return list of products in price range
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice, 
                                    @Param("maxPrice") java.math.BigDecimal maxPrice);
}
