-- ========================================
-- MANUAL DATABASE SETUP INSTRUCTIONS
-- ========================================
-- Run these commands in MySQL Workbench, CLI, or any MySQL client
-- BEFORE starting the Spring Boot application

-- Step 1: Create the database
CREATE DATABASE IF NOT EXISTS javacart CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Step 2: Use the database
USE javacart;

-- Step 3: Run the data below to populate sample data
-- (Tables will be auto-created by Hibernate when you run the app)
-- OR manually create tables using schema.sql

-- ========================================
-- SAMPLE DATA - RUN AFTER FIRST APP START
-- ========================================

-- Insert test users
-- Password for both: "password123" (BCrypt hashed)
INSERT INTO users (username, email, password_hash, role, created_at) VALUES
('testuser', 'test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', NOW()),
('admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', NOW());

-- Insert sample products
INSERT INTO products (name, description, price, stock, image_url, created_at) VALUES
('Laptop - Dell XPS 13', 'Ultra-portable 13-inch laptop with Intel i7 processor, 16GB RAM, 512GB SSD. Perfect for professionals and students.', 1299.99, 15, 'https://via.placeholder.com/400x300?text=Dell+XPS+13', NOW()),
('Wireless Mouse', 'Ergonomic wireless mouse with 6 buttons, adjustable DPI up to 3200, and rechargeable battery lasting 60 days.', 29.99, 50, 'https://via.placeholder.com/400x300?text=Wireless+Mouse', NOW()),
('Mechanical Keyboard', 'RGB backlit mechanical keyboard with Cherry MX Blue switches. Durable aluminum frame and programmable keys.', 89.99, 30, 'https://via.placeholder.com/400x300?text=Mechanical+Keyboard', NOW()),
('USB-C Hub', '7-in-1 USB-C hub with HDMI, USB 3.0 ports, SD card reader, and 100W power delivery. Compatible with MacBook and PC.', 49.99, 25, 'https://via.placeholder.com/400x300?text=USB-C+Hub', NOW()),
('Monitor - 27" 4K', '27-inch 4K UHD monitor with IPS panel, HDR400, 60Hz refresh rate, and VESA mount compatibility.', 399.99, 10, 'https://via.placeholder.com/400x300?text=4K+Monitor', NOW()),
('Webcam - 1080p', 'Full HD 1080p webcam with auto-focus, noise-canceling microphone, and wide-angle lens. Perfect for video calls.', 69.99, 40, 'https://via.placeholder.com/400x300?text=1080p+Webcam', NOW()),
('Laptop Stand', 'Adjustable aluminum laptop stand with ventilation, supports laptops up to 17 inches. Ergonomic design reduces neck strain.', 39.99, 60, 'https://via.placeholder.com/400x300?text=Laptop+Stand', NOW()),
('External SSD - 1TB', 'Portable 1TB external SSD with USB 3.2 Gen 2, read speeds up to 1050MB/s. Shock-resistant and compact.', 119.99, 35, 'https://via.placeholder.com/400x300?text=External+SSD', NOW()),
('Headphones - Noise Cancelling', 'Over-ear noise-cancelling headphones with 30-hour battery life, Bluetooth 5.0, and premium sound quality.', 199.99, 20, 'https://via.placeholder.com/400x300?text=NC+Headphones', NOW()),
('Phone Stand', 'Adjustable phone stand with 360-degree rotation, compatible with all smartphones and tablets. Non-slip base.', 14.99, 100, 'https://via.placeholder.com/400x300?text=Phone+Stand', NOW());

-- ========================================
-- VERIFY DATA
-- ========================================
SELECT * FROM users;
SELECT * FROM products;
