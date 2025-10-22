# 🧩 OOP PRINCIPLES IN JAVACART

This document explains how Object-Oriented Programming (OOP) principles are applied throughout the JavaCart application.

---

## 1. ENCAPSULATION

**Definition:** Bundling data (fields) and methods that operate on that data within a single unit (class), while hiding internal details.

### Examples in JavaCart:

#### Entity Classes (e.g., `User.java`)

```java
@Entity
public class User {
    @Id
    private Long id;              // Private field
    private String username;       // Private field
    private String passwordHash;   // Private field - NEVER exposed directly!
    
    // Getters and setters via Lombok @Data
    // External classes can't directly access fields
}
```

**Why?**
- Password hash is hidden from external access
- Only getters/setters can modify data (controlled access)
- Prevents accidental corruption of object state

#### Service Layer (e.g., `UserService.java`)

```java
@Service
public class UserService {
    private final UserRepository userRepository;  // Encapsulated dependency
    private final PasswordEncoder passwordEncoder; // Encapsulated dependency
    
    // Business logic is hidden inside methods
    public User registerUser(String username, String email, String rawPassword) {
        // Complex logic hidden from controllers
        // Hash password, validate, save to database
    }
}
```

**Why?**
- Controllers don't need to know HOW password hashing works
- They just call `registerUser()` and trust the service to handle it correctly

---

## 2. ABSTRACTION

**Definition:** Hiding complex implementation details and exposing only essential features through interfaces or abstract classes.

### Examples in JavaCart:

#### Repository Interfaces

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    // No SQL code visible! Spring Data JPA handles implementation
}
```

**What's abstracted?**
- SQL query generation
- Connection management
- Transaction handling
- Result set mapping

**Controller just uses it:**
```java
Optional<User> user = userRepository.findByUsername("john");
// Don't care if it's MySQL, PostgreSQL, or MongoDB!
```

#### Service Interfaces (Implicit)

While we didn't create explicit interfaces (to keep it beginner-friendly), services abstract business logic:

```java
// Controller doesn't know about BCrypt, validation, or database details
userService.registerUser(username, email, password);
```

---

## 3. SEPARATION OF CONCERNS

**Definition:** Each class/layer should have ONE responsibility. Don't mix different concerns in the same place.

### JavaCart's Layered Architecture:

```
┌─────────────────────┐
│    CONTROLLER       │  ← HTTP Requests/Responses
│  (Presentation)     │     URL routing, Model preparation
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│     SERVICE         │  ← Business Logic
│  (Business Logic)   │     Validation, transactions, rules
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│    REPOSITORY       │  ← Data Access
│  (Data Access)      │     SQL queries, CRUD operations
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│      ENTITY         │  ← Data Structure
│  (Domain Model)     │     Represents database tables
└─────────────────────┘
```

### Example Flow:

**Bad (Violates Separation of Concerns):**
```java
// Controller doing EVERYTHING - BAD! ❌
@PostMapping("/checkout")
public String checkout() {
    // SQL query directly in controller - BAD!
    jdbcTemplate.update("UPDATE products SET stock = stock - 1");
    
    // Business logic in controller - BAD!
    if (stock < 0) { /* validation */ }
    
    // Password hashing in controller - BAD!
    String hash = BCrypt.hashpw(password, BCrypt.gensalt());
}
```

**Good (Proper Separation):**
```java
// Controller - only HTTP concerns ✅
@PostMapping("/checkout")
public String checkout(Authentication auth) {
    User user = getCurrentUser(auth);
    orderService.checkout(user);  // Delegate to service!
    return "redirect:/orders";
}

// Service - only business logic ✅
@Transactional
public Order checkout(User user) {
    validateCart();      // Business rule
    createOrder();       // Business logic
    updateStock();       // Business logic
    return order;
}

// Repository - only data access ✅
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Just database queries
}
```

---

## 4. DEPENDENCY INJECTION (IoC)

**Definition:** Objects don't create their dependencies; dependencies are injected from outside (by Spring container).

### Without DI (Bad):

```java
// Hard-coded dependency - BAD! ❌
public class ProductController {
    private ProductService productService = new ProductService();  // Tight coupling!
}
```

**Problems:**
- Hard to test (can't mock)
- Hard to change implementation
- Violates Open/Closed Principle

### With DI (Good):

```java
// Constructor injection - GOOD! ✅
@Controller
public class ProductController {
    private final ProductService productService;
    
    // Spring automatically provides ProductService instance
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
}
```

**Benefits:**
- Easy to test (inject mock service)
- Easy to swap implementations
- Spring manages lifecycle

---

## 5. SINGLE RESPONSIBILITY PRINCIPLE (SRP)

**Definition:** A class should have ONE reason to change.

### Examples:

#### ✅ Good - Each Service Has ONE Job

```java
// UserService - ONLY user management
public class UserService {
    public User registerUser() { }
    public User findByUsername() { }
}

// ProductService - ONLY product management
public class ProductService {
    public List<Product> getAllProducts() { }
    public Product findById() { }
}

// CartService - ONLY cart management
public class CartService {
    public CartItem addToCart() { }
    public void clearCart() { }
}
```

#### ❌ Bad - God Class

```java
// Everything in one class - BAD!
public class ShoppingService {
    public User registerUser() { }        // User concern
    public Product getProduct() { }       // Product concern
    public void addToCart() { }           // Cart concern
    public void checkout() { }            // Order concern
}
// This class has FOUR reasons to change!
```

---

## 6. OPEN/CLOSED PRINCIPLE

**Definition:** Classes should be OPEN for extension but CLOSED for modification.

### Example: Repository Pattern

```java
// Base interface (closed for modification)
public interface JpaRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
}

// Extend with custom methods (open for extension)
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContaining(String name);  // Add new behavior
}
```

**Benefit:** We extended functionality WITHOUT modifying Spring Data JPA source code!

---

## 7. POLYMORPHISM

**Definition:** Many forms - ability to treat objects of different classes through a common interface.

### Example: UserDetailsService

```java
// Spring Security's interface
public interface UserDetailsService {
    UserDetails loadUserByUsername(String username);
}

// Our implementation
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) {
        // Our custom logic to load from database
    }
}
```

**Benefit:** Spring Security works with ANY implementation of `UserDetailsService`. It doesn't care if we load users from:
- MySQL (our case)
- MongoDB
- LDAP
- In-memory

Same interface, different implementations = polymorphism!

---

## 8. COMPOSITION OVER INHERITANCE

**Definition:** Prefer building objects from smaller pieces (composition) rather than inheriting from parent classes.

### Example: Service Composition

```java
@Service
public class OrderService {
    // Composed of other services (composition)
    private final CartService cartService;
    private final ProductService productService;
    private final OrderRepository orderRepository;
    
    // OrderService uses these services rather than inheriting from them
    @Transactional
    public Order checkout(User user) {
        cartService.getCartItems(user);      // Use composition
        productService.updateStock();         // Use composition
        orderRepository.save(order);          // Use composition
    }
}
```

**Why not inheritance?**
```java
// BAD - Inheritance creates tight coupling ❌
public class OrderService extends CartService {
    // Now we're stuck with all Cart methods/behaviors
    // Can't change CartService without affecting OrderService
}
```

---

## 9. TRANSACTIONAL INTEGRITY (Atomicity)

**Definition:** Related to ACID properties - all operations in a transaction succeed or all fail.

### Example: Checkout Process

```java
@Transactional  // All or nothing!
public Order checkout(User user) {
    // Step 1: Create order
    Order order = new Order();
    orderRepository.save(order);
    
    // Step 2: Create order items
    for (CartItem item : cartItems) {
        OrderItem orderItem = new OrderItem();
        orderItemRepository.save(orderItem);
    }
    
    // Step 3: Update stock
    productService.updateStock(productId, quantity);
    
    // Step 4: Clear cart
    cartService.clearCart(user);
    
    // If ANY step fails, ALL changes are rolled back!
    return order;
}
```

**Without @Transactional:**
- Order might be created but stock not updated → overselling
- Cart might be cleared but order not saved → lost order
- Partial data corruption

---

## 10. COHESION & COUPLING

### High Cohesion (Good) ✅

Each class has closely related responsibilities:

```java
// CartService - all methods relate to cart management
public class CartService {
    public CartItem addToCart() { }
    public void removeFromCart() { }
    public void updateQuantity() { }
    public List<CartItem> getCartItems() { }
    // All methods work with cart - high cohesion!
}
```

### Low Coupling (Good) ✅

Classes depend on abstractions, not concrete implementations:

```java
// Controller depends on Service interface (loose coupling)
public class ProductController {
    private final ProductService productService;  // Depends on abstraction
    
    // Can easily swap ProductService implementation
    // Controller doesn't know about repositories or database
}
```

### High Coupling (Bad) ❌

```java
// Controller directly using repository - BAD!
public class ProductController {
    private final ProductRepository productRepository;
    
    @GetMapping("/products")
    public String list() {
        // Business logic in controller!
        List<Product> products = productRepository.findAll();
        products.removeIf(p -> p.getStock() == 0);  // Business rule here!
    }
}
```

---

## SUMMARY: OOP PRINCIPLES IN ACTION

| Principle                    | Where Applied                          |
|------------------------------|----------------------------------------|
| **Encapsulation**            | Private fields, getters/setters        |
| **Abstraction**              | Repository interfaces, service layer   |
| **Separation of Concerns**   | MVC layers (Controller/Service/Repo)   |
| **Dependency Injection**     | Constructor injection in all layers    |
| **Single Responsibility**    | One service per domain (User, Product) |
| **Open/Closed**              | Extending repositories, not modifying  |
| **Polymorphism**             | UserDetailsService implementations     |
| **Composition**              | Services composed from other services  |
| **Transactional Integrity**  | @Transactional on checkout             |
| **Cohesion & Coupling**      | High cohesion, low coupling            |

---

## KEY TAKEAWAYS

1. **Layered architecture** enforces separation of concerns
2. **Dependency injection** makes code testable and flexible
3. **Transactional boundaries** prevent data corruption
4. **Abstraction** hides complexity from calling code
5. **Composition** creates flexible, maintainable systems

**Every design decision in JavaCart follows these OOP principles!**
