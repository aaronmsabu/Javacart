# 🚀 JAVACART - QUICK START GUIDE

## ✅ Pre-Flight Checklist

Before running the application, complete these steps:

---

## STEP 1: Install Prerequisites

Ensure you have installed:

- [ ] **Java 17** or higher (`java -version`)
- [ ] **Maven 3.6+** (`mvn -version`)
- [ ] **MySQL 8.0+** (running on `localhost:3306`)

---

## STEP 2: Setup MySQL Database

### A. Start MySQL Server

```bash
# macOS (with Homebrew)
brew services start mysql

# Or use MySQL preference pane / system service
```

### B. Create Database

Open Terminal and run:

```bash
mysql -u root -p
```

Enter your MySQL password, then run:

```sql
CREATE DATABASE javacart CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

**OR** use the provided script:

```bash
mysql -u root -p < setup-database.sql
```

---

## STEP 3: Configure Application

Edit `src/main/resources/application.properties`:

```properties
# Update YOUR_MYSQL_PASSWORD with your actual password
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

**⚠️ Important**: Replace `yourpassword` with your actual MySQL root password!

---

## STEP 4: Build the Project

In the `javacart` directory, run:

```bash
mvn clean install
```

**Expected output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
```

---

## STEP 5: Run the Application

### Option A: Maven (Recommended for Development)

```bash
mvn spring-boot:run
```

### Option B: JAR File

```bash
mvn clean package
java -jar target/javacart-1.0.0.jar
```

### Option C: VS Code / IDE

- Open `JavacartApplication.java`
- Click "Run" button or right-click → Run

---

## STEP 6: Verify Application Started

**Look for this in the console:**

```
Started JavacartApplication in X.XXX seconds (JVM running for X.XXX)
```

**Open browser and visit:** http://localhost:8080

You should see the product catalog page!

---

## STEP 7: Populate Sample Data

After the application starts successfully (tables are auto-created by Hibernate), stop the app and run:

```bash
mysql -u root -p javacart < setup-database.sql
```

This inserts:
- 2 test users (username: `testuser` and `admin`, password: `password123`)
- 10 sample products

**OR** register a new user via the UI (http://localhost:8080/register) instead of using test users.

---

## STEP 8: Restart and Test

1. **Restart the application:**
   ```bash
   mvn spring-boot:run
   ```

2. **Open browser:** http://localhost:8080

3. **Test the flow:**
   - Browse products
   - Click "Login" → username: `testuser`, password: `password123`
   - Click "Add to Cart" on any product
   - View cart at http://localhost:8080/cart
   - Click "Proceed to Checkout"
   - Confirm order
   - View order history at http://localhost:8080/orders

---

## 🎉 Success Indicators

✅ Application starts without errors  
✅ Products display on home page  
✅ Login works with test credentials  
✅ Can add products to cart  
✅ Checkout creates order and clears cart  
✅ Order appears in order history  

---

## 🐛 Common Issues

### Issue: "Access denied for user 'root'@'localhost'"

**Solution:** Update password in `application.properties`

---

### Issue: "Table 'javacart.users' doesn't exist"

**Solution:** 
1. Check `spring.jpa.hibernate.ddl-auto=update` in `application.properties`
2. Restart app (tables will be auto-created)
3. Run `setup-database.sql` to insert sample data

---

### Issue: Port 8080 already in use

**Solution:**
```bash
# macOS/Linux - kill process on port 8080
lsof -ti:8080 | xargs kill

# Or change port in application.properties
server.port=8081
```

---

### Issue: Products page is empty

**Solution:** Run `setup-database.sql` to insert sample products

---

## 📚 Next Steps

Once the app is running successfully:

1. **Explore the code:**
   - Read inline comments in each Java file
   - Understand MVC flow: Controller → Service → Repository → Entity
   
2. **Read documentation:**
   - `README.md` - Full project documentation
   - `TRANSACTIONS.md` - Transaction management explained

3. **Try features:**
   - Register a new user
   - Search products
   - Add multiple items to cart
   - Update quantities
   - Complete checkout
   - View order history

4. **Experiment:**
   - Add a new product manually in MySQL
   - Modify CSS styling in `static/css/style.css`
   - Add a new controller endpoint
   - Test concurrent checkouts (open two browser windows)

---

## 🔥 Testing Concurrent Checkout (Advanced)

To test transaction management:

1. Set product stock to 1 in MySQL:
   ```sql
   UPDATE products SET stock = 1 WHERE id = 1;
   ```

2. Open two browser windows (incognito + regular)

3. Log in as different users in each window

4. Add the same product to both carts

5. Click checkout simultaneously

6. **Expected:** One succeeds, one gets "Insufficient stock" error

This demonstrates transactional integrity!

---

## 🎓 Learning Resources

**Files to study in order:**

1. `pom.xml` - Dependencies
2. `JavacartApplication.java` - Entry point
3. `Entity classes` - Database structure
4. `Repository interfaces` - Data access
5. `Service classes` - Business logic
6. `Controller classes` - HTTP handling
7. `Thymeleaf templates` - Views

---

## 💡 Tips

- Use **Spring Boot DevTools** for hot reload (already included in `pom.xml`)
- Check `application.properties` for all configuration options
- View SQL queries in console (enabled by `spring.jpa.show-sql=true`)
- Use browser DevTools to inspect Thymeleaf-generated HTML

---

## 📞 Need Help?

If you encounter issues:

1. Check the **Troubleshooting** section in `README.md`
2. Review console logs for error messages
3. Verify MySQL is running and database exists
4. Ensure `application.properties` credentials are correct
5. Check that all dependencies installed successfully (`mvn clean install`)

---

**Congratulations!** 🎉 You now have a fully functional Spring Boot shopping cart application!

Happy coding! 🚀
