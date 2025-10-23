# JavaCart 🛒

A complete **Spring Boot Shopping Cart** application demonstrating MVC architecture, Spring Security, JPA/Hibernate, and Thymeleaf templating.

Built as a **beginner-friendly** learning project with extensive inline comments explaining OOP principles and Spring Boot concepts.

---

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [OOP Principles Applied](#oop-principles-applied)
- [Troubleshooting](#troubleshooting)
- [Git Workflow](#git-workflow)
- [Future Enhancements](#future-enhancements)
- [License](#license)

---

## ✨ Features

### Core Functionality
- **User Authentication**: Register, login, logout (session-based with Spring Security)
- **Product Catalog**: Browse products with search functionality
- **Shopping Cart**: Add/remove items, update quantities (persistent in database)
- **Checkout**: Transactional order processing with stock management
- **Order History**: View past orders with detailed line items

### Technical Features
- **MVC Architecture**: Clean separation of concerns (Controller → Service → Repository → Entity)
- **Spring Security**: BCrypt password hashing, role-based access control
- **JPA/Hibernate**: ORM for database operations with relationship mapping
- **Thymeleaf Templates**: Server-side rendering with reusable fragments
- **Transaction Management**: Atomic checkout process preventing data corruption
- **Responsive Design**: Mobile-friendly CSS layout

---

## 🛠️ Tech Stack

| Layer          | Technology                          |
|----------------|-------------------------------------|
| **Backend**    | Spring Boot 3.5.0, Java 21         |
| **Web**        | Spring MVC, Thymeleaf              |
| **Security**   | Spring Security (Form-based login) |
| **Persistence**| Spring Data JPA, Hibernate         |
| **Database**   | MySQL 8.0+                         |
| **Build Tool** | Maven 3.9.11                       |
| **Server**     | Embedded Tomcat 10.1.41            |

---

## 🏗️ Architecture

### MVC Pattern
```
User Request → Controller → Service → Repository → Database
                    ↓
                 Model
                    ↓
                Thymeleaf View → HTML Response
```

### Layer Responsibilities

1. **Entity Layer** (`com.javacart.entity`)
   - JPA entities with annotations
   - Represents database tables
   - Example: `User`, `Product`, `Order`

2. **Repository Layer** (`com.javacart.repository`)
   - Spring Data JPA interfaces
   - Handles database queries
   - Example: `UserRepository extends JpaRepository`

3. **Service Layer** (`com.javacart.service`)
   - Business logic and validation
   - Transactional operations
   - Example: `OrderService.checkout()`

4. **Controller Layer** (`com.javacart.controller`)
   - HTTP request handling
   - Model preparation for views
   - Example: `ProductController`, `CartController`

5. **View Layer** (`src/main/resources/templates`)
   - Thymeleaf HTML templates
   - CSS styling
   - Example: `products.html`, `cart.html`

---

## 📦 Prerequisites

Before running JavaCart, ensure you have:

1. **Java 21** (required - exact version)
   ```bash
   java -version
   # Should show: openjdk version "21.0.x" or similar
   ```
   
   **⚠️ Important**: This project requires **Java 21**. If you have multiple Java versions installed, you must use Java 21 specifically.

2. **Maven 3.9.11** or higher
   ```bash
   mvn -version
   ```

3. **MySQL 8.0+** or MySQL 9.x
   - Running on `localhost:3306`
   - With user `root` and password (update in `application.properties`)

4. **IDE** (optional but recommended)
   - IntelliJ IDEA, Eclipse, or VS Code with Java extensions

---

## 🚀 Installation & Setup

### Step 1: Clone the Repository

```bash
cd /path/to/your/workspace
git clone <repository-url>
cd javacart
```

### Step 2: Create MySQL Database

Open MySQL Workbench, CLI, or any MySQL client and run:

```sql
CREATE DATABASE javacart CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Or use the provided script:

```bash
mysql -u root -p < setup-database.sql
```

### Step 3: Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/javacart?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

**⚠️ Security Warning**: Never commit real passwords to version control! Use environment variables in production.

### Step 4: Install Dependencies

```bash
mvn clean install
```

This downloads all dependencies defined in `pom.xml`.

### Step 5: Populate Sample Data

**Option A: Run SQL manually**

After starting the app once (tables will be auto-created), run:

```bash
mysql -u root -p javacart < setup-database.sql
```

**Option B: Register via UI**

Skip SQL and register a new user through the web interface at `/register`.

---

## ▶️ Running the Application

### Prerequisites Check

**IMPORTANT**: This application requires Java 21. Before running, verify your Java version:

```bash
java -version
```

If you see Java 25, 17, 11, or any version other than 21, you need to switch to Java 21.

### Setting the Correct Java Version (macOS/Linux)

If you have multiple Java versions installed, set the correct one:

**macOS:**
```bash
# Check available Java versions
/usr/libexec/java_home -V

# Set Java 21 for current terminal session
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Verify it's set correctly
java -version
# Should show: openjdk version "21.0.x"
```

**Linux:**
```bash
# Check available Java versions
update-java-alternatives --list

# Set Java 21 (adjust path as needed)
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Verify
java -version
```

**Permanent Solution** (add to `~/.zshrc` or `~/.bashrc`):
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)  # macOS
# OR
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64  # Linux
```

### Method 1: Maven Command (Recommended)

```bash
# Ensure Java 21 is active
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Run the application
mvn spring-boot:run
```

### Method 2: Java JAR

```bash
# Build with Java 21
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
mvn clean package -DskipTests

# Run the JAR
java -jar target/javacart-1.0.0.jar
```

### Method 3: Background Process (Server Mode)

```bash
# Set Java 21 and run in background
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
mvn spring-boot:run > /dev/null 2>&1 &

# Check if running
lsof -ti:8080
```

### Method 4: IDE

**IntelliJ IDEA:**
1. Go to File → Project Structure → Project
2. Set Project SDK to Java 21
3. Right-click `JavacartApplication.java` → Run

**VS Code:**
1. Install "Extension Pack for Java"
2. Open `JavacartApplication.java`
3. Click "Run" above `main()` method

### Expected Output

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.5.0)

...
2025-10-23 10:00:00.000  INFO 12345 --- [main] c.j.JavacartApplication : Started JavacartApplication in 3.5 seconds
2025-10-23 10:00:00.001  INFO 12345 --- [main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
```

Application is now running at: **http://localhost:8080**

### Quick Start Commands (All-in-One)

**First Time Setup:**
```bash
# Create database
mysql -u root -p < setup-database.sql

# Set Java 21 and run
export JAVA_HOME=$(/usr/libexec/java_home -v 21) && mvn spring-boot:run
```

**Subsequent Runs:**
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21) && mvn spring-boot:run
```

---

## 📖 Usage

### Test Credentials (if you ran `setup-database.sql`)

| Username   | Password     | Role  |
|------------|--------------|-------|
| testuser   | password123  | USER  |
| admin      | password123  | ADMIN |

### User Flow

1. **Browse Products**
   - Visit http://localhost:8080
   - View product catalog
   - Use search bar to filter products

2. **Register/Login**
   - Click "Register" to create account
   - Or "Login" with test credentials

3. **Add to Cart**
   - Click "Add to Cart" on any product
   - Adjust quantities in cart

4. **Checkout**
   - Click "Proceed to Checkout"
   - Confirm order
   - Stock is decremented, cart is cleared

5. **View Orders**
   - Click "My Orders" to see order history
   - Click on any order to view details

### URLs

| Page            | URL                          | Auth Required |
|-----------------|------------------------------|---------------|
| Landing Page    | http://localhost:8080        | No            |
| Product Catalog | http://localhost:8080/products | No          |
| Product Detail  | http://localhost:8080/products/1 | No        |
| Login           | http://localhost:8080/login  | No            |
| Register        | http://localhost:8080/register | No          |
| Cart            | http://localhost:8080/cart   | Yes           |
| Checkout        | http://localhost:8080/checkout | Yes         |
| Order History   | http://localhost:8080/orders | Yes           |

---

## 📂 Project Structure

```
javacart/
├── src/
│   ├── main/
│   │   ├── java/com/javacart/
│   │   │   ├── config/               # Security & configuration
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   ├── controller/           # HTTP request handlers
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ProductController.java
│   │   │   │   ├── CartController.java
│   │   │   │   └── OrderController.java
│   │   │   ├── entity/               # JPA entities (database tables)
│   │   │   │   ├── User.java
│   │   │   │   ├── Product.java
│   │   │   │   ├── CartItem.java
│   │   │   │   ├── Order.java
│   │   │   │   └── OrderItem.java
│   │   │   ├── repository/           # Spring Data JPA repositories
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── ProductRepository.java
│   │   │   │   ├── CartItemRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   └── OrderItemRepository.java
│   │   │   ├── service/              # Business logic layer
│   │   │   │   ├── UserService.java
│   │   │   │   ├── ProductService.java
│   │   │   │   ├── CartService.java
│   │   │   │   └── OrderService.java
│   │   │   └── JavacartApplication.java  # Main entry point
│   │   └── resources/
│   │       ├── templates/            # Thymeleaf HTML templates
│   │       │   ├── fragments/
│   │       │   │   ├── header.html
│   │       │   │   └── footer.html
│   │       │   ├── products.html
│   │       │   ├── product-detail.html
│   │       │   ├── cart.html
│   │       │   ├── checkout.html
│   │       │   ├── orders.html
│   │       │   ├── order-detail.html
│   │       │   ├── login.html
│   │       │   └── register.html
│   │       ├── static/
│   │       │   └── css/
│   │       │       └── style.css     # Application styling
│   │       ├── application.properties # Configuration
│   │       ├── schema.sql            # Database schema (optional)
│   │       └── data.sql              # Sample data (optional)
├── setup-database.sql                # DB setup script
├── TRANSACTIONS.md                   # Concurrency documentation
├── pom.xml                           # Maven dependencies
└── README.md                         # This file
```

---

## 🧩 OOP Principles Applied

### 1. **Encapsulation**
- Private fields in entities
- Public getters/setters via Lombok `@Data`
- Example: `User` entity hides password hash

### 2. **Abstraction**
- Service interfaces abstract business logic from controllers
- JPA repositories abstract SQL queries
- Example: `CartService` hides cart management complexity

### 3. **Separation of Concerns**
- **Controllers**: Handle HTTP
- **Services**: Handle business rules
- **Repositories**: Handle database access
- **Entities**: Represent data structure

### 4. **Dependency Injection**
- Constructor injection of dependencies
- Spring manages object lifecycle
- Example: `ProductController` receives `ProductService` via constructor

### 5. **Single Responsibility Principle**
- Each class has one job
- Example: `UserService` only manages users (not products or orders)

### 6. **Transactional Atomicity**
- `@Transactional` ensures all-or-nothing operations
- Example: `OrderService.checkout()` either completes fully or rolls back

---

## 🐛 Troubleshooting

### Issue: Wrong Java Version Error

**Symptom:**
```
Unsupported class file major version 65
```
or
```
java.lang.UnsupportedClassVersionError
```

**Solution:**
This means you're using the wrong Java version. The app requires Java 21.

```bash
# Check your current Java version
java -version

# Set to Java 21 (macOS)
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Verify
java -version
# Should show: openjdk version "21.0.x"

# Re-run the app
mvn clean spring-boot:run
```

If Java 21 is not installed, download it from:
- **Temurin**: https://adoptium.net/temurin/releases/?version=21
- **Oracle**: https://www.oracle.com/java/technologies/downloads/#java21

---

### Issue: Application won't start

**Symptom:**
```
Error creating bean with name 'dataSource'
```

**Solution:**
- Check MySQL is running: `mysql -u root -p`
- Verify `application.properties` credentials
- Ensure database `javacart` exists

---

### Issue: "Table doesn't exist"

**Symptom:**
```
Table 'javacart.users' doesn't exist
```

**Solution:**
- Set `spring.jpa.hibernate.ddl-auto=update` in `application.properties`
- Restart application (Hibernate will create tables)
- Or manually run `schema.sql`

---

### Issue: Login fails with valid credentials

**Symptom:**
Login page shows "Invalid username or password"

**Solution:**
- Check that users table has data: `SELECT * FROM users;`
- Verify password is BCrypt hashed (starts with `$2a$`)
- If you manually inserted users, re-run `setup-database.sql`

---

### Issue: Port 8080 already in use

**Symptom:**
```
Port 8080 was already in use
```

**Solution:**
- Kill process on port 8080: `lsof -ti:8080 | xargs kill` (macOS/Linux)
- Or change port in `application.properties`: `server.port=8081`

---

### Issue: Products not displaying

**Symptom:**
Product page is empty

**Solution:**
- Run `setup-database.sql` to insert sample products
- Check database: `SELECT * FROM products;`
- Or add products manually via SQL

---

## 🔄 Git Workflow

### Suggested Commit Structure

```bash
# Initial setup
git add .
git commit -m "chore: bootstrap Spring Boot project with Maven"

# Add entities
git add src/main/java/com/javacart/entity/
git commit -m "feat: add JPA entity classes (User, Product, CartItem, Order, OrderItem)"

# Add repositories
git add src/main/java/com/javacart/repository/
git commit -m "feat: add Spring Data JPA repositories"

# Add services
git add src/main/java/com/javacart/service/
git commit -m "feat: implement service layer with business logic"

# Add security
git add src/main/java/com/javacart/config/
git commit -m "feat: configure Spring Security with form login"

# Add controllers
git add src/main/java/com/javacart/controller/
git commit -m "feat: add MVC controllers for product, cart, and order"

# Add views
git add src/main/resources/templates/ src/main/resources/static/
git commit -m "feat: add Thymeleaf templates and CSS styling"

# Documentation
git add README.md TRANSACTIONS.md
git commit -m "docs: add comprehensive README and transaction documentation"
```

### `.gitignore`

See `.gitignore` file (generated below) for files to exclude from version control.

---

## 🚀 Future Enhancements

Ideas for extending JavaCart:

1. **Payment Integration**: Stripe, PayPal
2. **Admin Panel**: Manage products, users, orders
3. **Product Reviews**: Rating and review system
4. **Wishlist**: Save products for later
5. **Email Notifications**: Order confirmation emails
6. **Product Categories**: Organize products by type
7. **Search Filters**: Price range, rating, availability
8. **Password Reset**: Forgot password functionality
9. **REST API**: Expose endpoints for mobile app
10. **Docker**: Containerize application for deployment

---

## 📄 License

This project is created for **educational purposes**.  
Feel free to use, modify, and learn from it.

---

## 👨‍💻 Author

Built with ❤️ as a learning project to demonstrate:
- Spring Boot best practices
- MVC architecture
- Transaction management
- Security fundamentals

**Questions or suggestions?** Open an issue or submit a pull request!

---

## 🙏 Acknowledgments

- Spring Boot Documentation
- Baeldung Spring Tutorials
- Thymeleaf Documentation
- Stack Overflow Community

---

**Happy Coding! 🚀**
