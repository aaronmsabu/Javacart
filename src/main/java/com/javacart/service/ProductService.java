package com.javacart.service;

import com.javacart.entity.Product;
import com.javacart.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * ProductService manages product catalog operations.
 * 
 * Responsibilities:
 * - List all products
 * - Search products by keyword
 * - Get product details
 * - Update stock levels (during checkout)
 * - Validate product availability
 * 
 * Note: Product creation/update/delete (admin features) not implemented
 * in this beginner-friendly app. Can be added later.
 */
@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    
    /**
     * Constructor injection of ProductRepository.
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    /**
     * Get all products in the catalog.
     * Used for the main product listing page.
     * 
     * @return list of all products
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    /**
     * Get products sorted by name alphabetically.
     * Provides a better user experience than unsorted list.
     * 
     * @return list of products sorted by name
     */
    public List<Product> getAllProductsSorted() {
        return productRepository.findAllByOrderByNameAsc();
    }
    
    /**
     * Find a product by ID.
     * Used for product detail page and cart operations.
     * 
     * @param id product ID
     * @return Optional containing Product if found
     */
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
    
    /**
     * Search products by keyword (searches name and description).
     * Used for search functionality on product listing page.
     * 
     * @param keyword search term
     * @return list of matching products
     */
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.searchProducts(keyword.trim());
    }
    
    /**
     * Find products by name (partial match, case-insensitive).
     * Alternative search method using derived query.
     * 
     * @param name search term
     * @return list of matching products
     */
    public List<Product> findByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Get only products that are in stock.
     * Useful for filtering out unavailable items.
     * 
     * @return list of products with stock > 0
     */
    public List<Product> getInStockProducts() {
        return productRepository.findByStockGreaterThan(0);
    }
    
    /**
     * Find products within a price range.
     * Used for price filter functionality.
     * 
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return list of products in range
     */
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }
    
    /**
     * Update product stock level.
     * Called during checkout to decrement stock.
     * 
     * Why @Transactional?
     * - Ensures database consistency
     * - If multiple stock updates fail, all are rolled back
     * - Prevents overselling due to concurrent orders
     * 
     * @param productId the product to update
     * @param quantity amount to subtract from stock
     * @return true if update successful, false if insufficient stock
     */
    @Transactional
    public boolean updateStock(Long productId, int quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return false;
        }
        
        Product product = productOpt.get();
        
        // Check if sufficient stock is available
        if (!product.hasStock(quantity)) {
            return false;
        }
        
        // Decrement stock
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
        
        return true;
    }
    
    /**
     * Check if a product has sufficient stock for purchase.
     * Used to validate cart items before checkout.
     * 
     * @param productId the product ID
     * @param quantity requested quantity
     * @return true if stock is sufficient
     */
    public boolean hasStock(Long productId, int quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        return productOpt.isPresent() && productOpt.get().hasStock(quantity);
    }
    
    /**
     * Save or update a product (for admin features).
     * Not used in this basic app but included for completeness.
     * 
     * @param product the product to save
     * @return saved product
     */
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
}
