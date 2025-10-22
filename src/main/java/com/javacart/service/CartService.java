package com.javacart.service;

import com.javacart.entity.CartItem;
import com.javacart.entity.Product;
import com.javacart.entity.User;
import com.javacart.repository.CartItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * CartService manages shopping cart operations.
 * 
 * Cart Design Decision:
 * - Stock is NOT decremented when adding to cart
 * - Stock is only decremented during checkout
 * - This avoids "reserving" items and blocking other customers
 * - Tradeoff: Multiple users can add same out-of-stock item to cart,
 *   but checkout will fail gracefully with clear error message
 * 
 * Alternative Approach (not implemented here):
 * - Reserve stock when adding to cart with timeout
 * - Requires background job to release abandoned carts
 * - More complex but prevents checkout failures
 * 
 * Why @Transactional?
 * - Ensures atomic operations (all-or-nothing)
 * - If database write fails midway, changes are rolled back
 * - Prevents cart data corruption
 */
@Service
public class CartService {
    
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    
    /**
     * Constructor injection of dependencies.
     */
    public CartService(CartItemRepository cartItemRepository, ProductService productService) {
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
    }
    
    /**
     * Add a product to user's cart or update quantity if already in cart.
     * 
     * Logic:
     * 1. Check if product exists
     * 2. Check if product already in cart
     *    - If yes: increase quantity
     *    - If no: create new cart item
     * 3. Save to database
     * 
     * Note: We don't validate stock here (validated at checkout instead).
     * This allows users to add items even if temporarily out of stock,
     * in case stock is replenished before they checkout.
     * 
     * @param user the user adding to cart
     * @param productId the product to add
     * @param quantity quantity to add (must be > 0)
     * @return the created or updated CartItem
     * @throws IllegalArgumentException if product not found or quantity invalid
     */
    @Transactional
    public CartItem addToCart(User user, Long productId, int quantity) {
        // Validate quantity
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        
        // Find the product
        Product product = productService.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        // Check if product already in cart
        Optional<CartItem> existingItemOpt = cartItemRepository.findByUserAndProduct(user, product);
        
        if (existingItemOpt.isPresent()) {
            // Update existing cart item (increase quantity)
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return cartItemRepository.save(existingItem);
        } else {
            // Create new cart item
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            return cartItemRepository.save(newItem);
        }
    }
    
    /**
     * Update quantity of a cart item.
     * If quantity is 0 or negative, remove the item from cart.
     * 
     * @param cartItemId the cart item to update
     * @param quantity new quantity (if <= 0, item is removed)
     * @return updated CartItem, or null if removed
     * @throws IllegalArgumentException if cart item not found
     */
    @Transactional
    public CartItem updateQuantity(Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        
        if (quantity <= 0) {
            // Remove item if quantity is 0 or negative
            cartItemRepository.delete(cartItem);
            return null;
        }
        
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }
    
    /**
     * Remove a product from the cart.
     * 
     * @param cartItemId the cart item to remove
     * @throws IllegalArgumentException if cart item not found
     */
    @Transactional
    public void removeFromCart(Long cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new IllegalArgumentException("Cart item not found");
        }
        cartItemRepository.deleteById(cartItemId);
    }
    
    /**
     * Get all items in user's cart.
     * 
     * @param user the user
     * @return list of cart items (empty if cart is empty)
     */
    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }
    
    /**
     * Calculate total price of all items in cart.
     * Sum of (price × quantity) for each item.
     * 
     * @param user the user
     * @return total cart value
     */
    public BigDecimal calculateCartTotal(User user) {
        return cartItemRepository.calculateCartTotal(user.getId());
    }
    
    /**
     * Clear all items from user's cart.
     * Called after successful checkout.
     * 
     * @param user the user whose cart to clear
     */
    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }
    
    /**
     * Count number of items in cart.
     * Used to display cart badge count in navbar.
     * 
     * @param user the user
     * @return number of distinct products in cart
     */
    public long getCartItemCount(User user) {
        return cartItemRepository.countByUser(user);
    }
    
    /**
     * Validate that all items in cart have sufficient stock.
     * Called before checkout to ensure order can be fulfilled.
     * 
     * @param user the user
     * @return true if all items have sufficient stock, false otherwise
     */
    public boolean validateCartStock(User user) {
        List<CartItem> cartItems = getCartItems(user);
        
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (!product.hasStock(item.getQuantity())) {
                return false; // Insufficient stock for at least one item
            }
        }
        
        return true; // All items have sufficient stock
    }
}
