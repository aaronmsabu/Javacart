# 🚀 JAVACART - QUICK START GUIDE

## ✅ Pre-Flight Checklist

Before running the application, complete these steps:

**IMPORTANT**: This application requires **Java 21** specifically. Using Java 17, 25, or any other version will cause compilation errors.

---

## STEP 1: Install Prerequisites

Ensure you have installed:

- [ ] **Java 21** (exact version required) - `java -version` should show "21.0.x"
- [ ] **Maven 3.9.11+** (`mvn -version`)
- [ ] **MySQL 8.0+** or MySQL 9.x (running on `localhost:3306`)

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

## STEP 4: Verify Java Version

**CRITICAL**: Before building, ensure you're using Java 21.

```bash
java -version
```

**Expected output:**
```
openjdk version "21.0.5" 2024-10-15
OpenJDK Runtime Environment Temurin-21.0.5+11 (build 21.0.5+11)
OpenJDK 64-Bit Server VM Temurin-21.0.5+11 (build 21.0.5+11, mixed mode)
```

### If you see a different version (e.g., Java 25, 17, 11):

**macOS:**
```bash
# List all installed Java versions
/usr/libexec/java_home -V

# Output should show something like:
# 21.0.5 (x86_64) "Eclipse Temurin 21" - "/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"

# Set Java 21 for current session
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Verify it worked
java -version
```

**Linux:**
```bash
# Check available versions
update-java-alternatives --list

# Set Java 21 (adjust path based on your installation)
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Verify
java -version
```

### Make it Permanent (Optional)

Add to your shell profile (`~/.zshrc` for macOS, `~/.bashrc` for Linux):

```bash
# macOS
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Linux (adjust path)
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

Then reload your shell:
```bash
source ~/.zshrc  # or source ~/.bashrc
```

---

## STEP 5: Configure Application

Edit `src/main/resources/application.properties`:

```properties
# Update YOUR_MYSQL_PASSWORD with your actual password
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

**⚠️ Important**: Replace `yourpassword` with your actual MySQL root password!

---

## STEP 6: Build the Project

In the `javacart` directory, with Java 21 active:

```bash
# Ensure Java 21 is set (if not permanent)
export JAVA_HOME=$(/usr/libexec/java_home -v 21)  # macOS
# OR
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64  # Linux

# Clean build
mvn clean install
```

**Expected output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
[INFO] Finished at: 2025-10-23T10:00:00-07:00
```

**If you see errors about class version**, you're not using Java 21. Go back to STEP 4.

---

## STEP 7: Run the Application

### Option A: Maven (Recommended)

```bash
# Ensure Java 21 is active
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Run the app
mvn spring-boot:run
```

### Option B: JAR File

```bash
# Build (if not already done)
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
mvn clean package -DskipTests

# Run the JAR
java -jar target/javacart-1.0.0.jar
```

### Option C: Background Process (Server Mode)

```bash
# Set Java 21 and run in background
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
mvn spring-boot:run > /dev/null 2>&1 &

# The app runs in background
# To stop it later: lsof -ti:8080 | xargs kill -9
```

### Option D: VS Code / IDE

**IntelliJ IDEA:**
1. File → Project Structure → Project → Set SDK to Java 21
2. Right-click `JavacartApplication.java` → Run

**VS Code:**
1. Install "Extension Pack for Java"
2. Set Java 21 in settings
3. Open `JavacartApplication.java` → Click "Run" above `main()`

---

## STEP 8: Verify Application Started

**Look for this in the console:**

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.5.0)

...
Started JavacartApplication in 3.5 seconds (JVM running for 4.2)
Tomcat started on port 8080 (http) with context path ''
```

**Open browser and visit:** 
- **Landing Page**: http://localhost:8080
- **Product Catalog**: http://localhost:8080/products

You should see the premium landing page with animated hero section!

---

## STEP 9: Populate Sample Data

After the application starts successfully (tables are auto-created by Hibernate), stop the app and run:

```bash
mysql -u root -p javacart < setup-database.sql
```

This inserts:
- 2 test users (username: `testuser` and `admin`, password: `password123`)
- 10 sample products

**OR** register a new user via the UI (http://localhost:8080/register) instead of using test users.

---

## STEP 10: Restart and Test

1. **Restart the application:**
   ```bash
   export JAVA_HOME=$(/usr/libexec/java_home -v 21)
   mvn spring-boot:run
   ```

2. **Open browser:** 
   - Landing Page: http://localhost:8080
   - Product Catalog: http://localhost:8080/products

3. **Test the flow:**
   - Browse premium landing page with animated hero
   - Click "View Products" or "Shop Now"
   - Use search bar to filter products
   - Click "Login" → username: `testuser`, password: `password123`
   - Click "Add to Cart" on any product
   - View cart at http://localhost:8080/cart
   - Click "Proceed to Checkout"
   - Confirm order
   - View order history at http://localhost:8080/orders

---

## 🎉 Success Indicators

✅ Application starts without errors  
✅ Console shows "Spring Boot :: (v3.5.0)"  
✅ Landing page displays with animated hero section  
✅ Products display on catalog page (/products)  
✅ Search bar works with glass morphism design  
✅ Login works with test credentials  
✅ Can add products to cart  
✅ Cart shows badge count in navigation  
✅ Checkout creates order and clears cart  
✅ Order appears in order history  

---

## 🐛 Common Issues

### Issue: "Unsupported class file major version 65"

**Solution:** You're not using Java 21. Run these commands:

```bash
# macOS
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Linux
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Verify
java -version  # Must show 21.0.x

# Clean rebuild
mvn clean install

# Run
mvn spring-boot:run
```

---

### Issue: "Could not find or load main class"

**Solution:** Ensure you're in the `javacart` directory (where `pom.xml` is located):

```bash
cd /path/to/javacart
mvn spring-boot:run
```

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

## � Quick Commands Reference

### First Time Setup (Complete Flow)

```bash
# 1. Set Java 21
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# 2. Verify Java version
java -version  # Must show 21.0.x

# 3. Create database
mysql -u root -p < setup-database.sql

# 4. Build project
mvn clean install

# 5. Run application
mvn spring-boot:run
```

### Daily Development (Quick Start)

```bash
# One-liner to run the app
export JAVA_HOME=$(/usr/libexec/java_home -v 21) && mvn spring-boot:run
```

### Background Mode (Server)

```bash
# Start in background
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
mvn spring-boot:run > /dev/null 2>&1 &

# Check if running
lsof -ti:8080

# Stop the server
lsof -ti:8080 | xargs kill -9
```

### Rebuild After Changes

```bash
# Stop server if running
lsof -ti:8080 | xargs kill -9

# Clean rebuild
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
mvn clean package -DskipTests

# Restart
mvn spring-boot:run
```

---

## �📚 Next Steps

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
