package com.javacart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the JavaCart application.
 * 
 * @SpringBootApplication is a convenience annotation that combines:
 * - @Configuration: Marks this class as a source of bean definitions
 * - @EnableAutoConfiguration: Enables Spring Boot's auto-configuration mechanism
 * - @ComponentScan: Scans for components, configurations, and services in this package
 * 
 * This annotation tells Spring Boot to set up the entire application context,
 * configure the embedded Tomcat server, and wire up all components automatically.
 */
@SpringBootApplication
public class JavacartApplication {

    /**
     * Application entry point - starts the Spring Boot application.
     * 
     * @param args Command line arguments (not used in this app)
     */
    public static void main(String[] args) {
        SpringApplication.run(JavacartApplication.class, args);
    }
}
