package com.example.demo.config;

import com.example.demo.service.UserService;  // Assuming you have a custom UserService
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    // Constructor injection for UserService
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Disable CSRF for simplicity (modify based on your needs)
            .authorizeRequests()
            // Public URLs accessible to all users
            .requestMatchers("/register", "/login","/logout","/update-profile","/post/{id}/like","/post/{id}/comment","/admindash","adminlogin","/explore","login-receiver","/update-request","/register-receiver","/verify-otp","/profile","/request-donor","/donor-list","/donor-dashboard", "/forgot-password", "/reset-password", "/about", "/profile", "/", "/blogs", "/create-post", "/post/**").permitAll()
            .requestMatchers("/post/**","/comments/**","/like/**","/view-proof/**").permitAll() 
            // Admin-only paths, restrict access to users with 'ADMIN' role
            .requestMatchers("/admin").authenticated() // Restricts "/admin"
                .anyRequest().authenticated()  // Any other requests must be authenticated
            .and()
            .formLogin()
                .loginPage("/ll")  // Custom login page URL
                .permitAll()
            .and()
            .logout()
            .logoutUrl("/logout") // Handle logout requests
            .logoutSuccessUrl("/login") // Redirect to login after logout
            .invalidateHttpSession(true) // Invalidate session
            .deleteCookies("JSESSIONID"); // Delete session cookies
        
        
        return http.build();  // Must return SecurityFilterChain
    }

    // Define the PasswordEncoder bean (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
