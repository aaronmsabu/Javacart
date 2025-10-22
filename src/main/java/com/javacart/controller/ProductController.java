package com.javacart.controller;

import com.javacart.entity.Product;
import com.javacart.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * ProductController handles product catalog display.
 * 
 * Endpoints:
 * - GET / or /products - List all products (with optional search)
 * - GET /products/{id} - Product detail page
 * 
 * MVC Pattern:
 * - Controller receives HTTP request
 * - Controller calls Service layer for business logic
 * - Controller adds data to Model
 * - Controller returns view name (Thymeleaf template)
 * - Thymeleaf renders HTML with model data
 * 
 * Public Access:
 * - These endpoints are public (no login required)
 * - Anyone can browse products
 * - Must log in to add to cart or checkout
 */
@Controller
public class ProductController {
    
    private final ProductService productService;
    
    /**
     * Constructor injection of ProductService.
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * Display product listing page (home page).
     * 
     * Supports optional search parameter:
     * - /products - show all products
     * - /products?search=laptop - show products matching "laptop"
     * 
     * Model Attributes:
     * - products: List of products to display
     * - searchTerm: Current search keyword (if any)
     * 
     * @param search optional search keyword
     * @param model Spring MVC model for passing data to view
     * @return template name "products" (renders products.html)
     */
    @GetMapping({"/", "/products"})
    public String listProducts(@RequestParam(value = "search", required = false) String search,
                              Model model) {
        List<Product> products;
        
        if (search != null && !search.trim().isEmpty()) {
            // Search products by keyword
            products = productService.searchProducts(search.trim());
            model.addAttribute("searchTerm", search.trim());
        } else {
            // Show all products
            products = productService.getAllProductsSorted();
        }
        
        model.addAttribute("products", products);
        return "products"; // Renders src/main/resources/templates/products.html
    }
    
    /**
     * Display product detail page.
     * 
     * Shows detailed information about a single product:
     * - Name, description, price
     * - Stock availability
     * - Add to cart button
     * 
     * Path Variable:
     * - /products/1 - product with ID 1
     * - /products/5 - product with ID 5
     * 
     * Error Handling:
     * - If product not found, redirect to product list with error message
     * 
     * @param id product ID from URL path
     * @param model Spring MVC model for passing data to view
     * @return template name "product-detail" or redirect
     */
    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id).orElse(null);
        
        if (product == null) {
            // Product not found - redirect to product list with error
            return "redirect:/products?error=notfound";
        }
        
        model.addAttribute("product", product);
        return "product-detail"; // Renders src/main/resources/templates/product-detail.html
    }
}
