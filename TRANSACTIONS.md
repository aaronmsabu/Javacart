# TRANSACTION MANAGEMENT & CONCURRENCY

## Overview

This document explains how JavaCart handles transactions and prevents data corruption in concurrent scenarios.

## Transaction Management

### What is a Transaction?

A transaction is a sequence of database operations that must either:
- **All succeed** (commit)
- **All fail** (rollback)

This ensures data consistency (ACID properties: Atomicity, Consistency, Isolation, Durability).

### Where We Use Transactions

#### 1. **Checkout Process** (CRITICAL)

**File:** `OrderService.java` - `checkout()` method

**Why transactional?**
The checkout process involves multiple database operations:
1. Create Order
2. Create OrderItems (multiple inserts)
3. Decrement product stock (multiple updates)
4. Clear cart items (delete)

If ANY of these fail, the entire operation is rolled back.

**Example:**
```java
@Transactional
public Order checkout(User user) {
    // 1. Validate cart
    // 2. Create order
    // 3. Create order items
    // 4. Update stock
    // 5. Clear cart
    // If any step fails, ALL changes are rolled back
}
```

**What happens without @Transactional?**
- Order might be created but stock not updated → overselling
- Cart might be cleared but order not saved → lost order
- OrderItems created but Order creation fails → orphaned data

#### 2. **Stock Updates**

**File:** `ProductService.java` - `updateStock()` method

**Why transactional?**
Ensures stock is atomically decremented. Prevents race conditions where multiple threads read the same stock value and decrement simultaneously.

#### 3. **Cart Operations**

**File:** `CartService.java` - `addToCart()`, `updateQuantity()`, `clearCart()`

**Why transactional?**
Ensures cart modifications are atomic. If quantity update fails, no partial updates occur.

---

## Concurrency Issues & Solutions

### Problem 1: Race Condition on Product Stock

**Scenario:**
Two users try to buy the last item simultaneously:
1. User A reads: stock = 1
2. User B reads: stock = 1
3. User A buys: stock = 0
4. User B buys: stock = -1 ❌ (overselling!)

**Our Solution: Check Inside Transaction**
```java
@Transactional
public Order checkout(User user) {
    // Validate stock INSIDE the transaction
    for (CartItem item : cartItems) {
        if (!product.hasStock(quantity)) {
            throw new IllegalStateException("Insufficient stock");
        }
    }
    // Then decrement
    productService.updateStock(productId, quantity);
}
```

**Why this works:**
- Transaction isolation ensures no other transaction can modify stock between check and update
- If two transactions run concurrently, one will fail (second read will see updated stock)

**Limitation:**
- Uses default isolation level (READ_COMMITTED)
- Not 100% foolproof under extreme concurrency

**Alternative Solutions (not implemented):**
1. **Pessimistic Locking**: Lock rows during read
   ```java
   @Lock(LockModeType.PESSIMISTIC_WRITE)
   Product findById(Long id);
   ```

2. **Optimistic Locking**: Use version field
   ```java
   @Entity
   public class Product {
       @Version
       private Long version;
   }
   ```

3. **Database Constraints**: Add `CHECK (stock >= 0)` constraint in MySQL

### Problem 2: Cart Item Duplication

**Scenario:**
User clicks "Add to Cart" twice rapidly → creates two cart items for same product

**Our Solution: Unique Constraint**
```sql
UNIQUE KEY unique_user_product (user_id, product_id)
```

Database prevents duplicate rows. If duplicate insert attempted, exception is thrown.

### Problem 3: Dirty Reads

**Scenario:**
User A is checking out. User B views the same product page and sees incorrect stock.

**Our Solution: Default Isolation Level**
- Spring Boot uses READ_COMMITTED by default
- User B will only see committed changes
- While User A's transaction is in progress, User B sees old stock value
- After User A commits, User B sees updated stock

---

## Testing Concurrency (Manual)

### Test 1: Simultaneous Checkout

**Setup:**
1. Product has stock = 1
2. Two users add to cart

**Test:**
- Both users click checkout simultaneously
- Expected: One succeeds, one gets "Insufficient stock" error

**How to test:**
Open two browser tabs, log in as different users, and checkout at the same time.

### Test 2: Stock Doesn't Go Negative

**Setup:**
Product has stock = 5

**Test:**
- 10 users try to buy 1 each simultaneously
- Expected: First 5 succeed, next 5 fail

---

## Best Practices Applied

1. ✅ **Use `@Transactional` on service methods** (not controllers)
2. ✅ **Keep transactions short** (only database operations)
3. ✅ **Validate before modifying** (check stock before decrementing)
4. ✅ **Use database constraints** (unique keys, foreign keys)
5. ✅ **Handle exceptions gracefully** (catch and show user-friendly errors)

---

## What We Didn't Implement (Advanced)

For a production-scale application, consider:

1. **Distributed Transactions**: If using multiple databases or microservices
2. **Message Queues**: For asynchronous order processing
3. **Redis Locking**: For distributed locks across multiple server instances
4. **Retry Logic**: Automatically retry failed transactions
5. **Circuit Breakers**: Prevent cascading failures

---

## Key Takeaway

**Transactions ensure data consistency.**  
**Concurrency handling prevents race conditions.**  
**Together, they make the checkout process reliable and safe.**
