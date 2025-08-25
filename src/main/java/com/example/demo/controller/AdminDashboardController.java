package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.RequestRepository;
import com.example.demo.repository.ReceiverRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AdminDashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReceiverRepository receiverRepository;

    @Autowired
    private RequestRepository requestRepository;

   
    
    @GetMapping("/adminlogin")
    public String showLoginPage() {
        return "adminlogin"; // Thymeleaf template name (admin-login.html)
    }
    @PostMapping("/adminlogin")
    public String loginAdmin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model) {

        // Hardcoded admin credentials
        String adminUsername = "admin";
        String adminPassword = "admin";

        // Validate the credentials
        if (username.equals(adminUsername) && password.equals(adminPassword)) {
            // Login successful: Redirect to admin dashboard
            model.addAttribute("username", username);
            return "redirect:/admindash"; // Thymeleaf template for admin dashboard
        }

        // Login failed: Return to login page with error message
        model.addAttribute("error", "Invalid username or password. Please try again.");
        return "adminlogin";
    }

    @GetMapping("/admindash")
    public String showDashboard(Model model) {
        // Fetch totals
        long totalDonors = userRepository.count();
        long totalReceivers = receiverRepository.count();
        long totalDonations = requestRepository.count();

        // Fetch donations by status
        long pendingDonations = requestRepository.findByStatus("Pending").size();
        long acceptedDonations = requestRepository.findByStatus("Accepted").size();
        long rejectedDonations = requestRepository.findByStatus("Rejected").size();

        // Fetch donors, receivers, and donations
        List<User> donors = userRepository.findAll();
        List<Receiver> receivers = receiverRepository.findAll();
        List<Request> donations = requestRepository.findAll();

        // Add attributes to the model
        model.addAttribute("totalDonors", totalDonors);
        model.addAttribute("totalReceivers", totalReceivers);
        model.addAttribute("totalDonations", totalDonations);
        model.addAttribute("pendingDonations", pendingDonations);
        model.addAttribute("acceptedDonations", acceptedDonations);
        model.addAttribute("rejectedDonations", rejectedDonations);
        model.addAttribute("donors", donors);
        model.addAttribute("receivers", receivers);
        model.addAttribute("donations", donations);

        return "admin";  // Thymeleaf template
    }

    private Map<Object, Long> someMethod() {
        // Logic to generate and return the map
        return requestRepository.findAll()
            .stream()
            .collect(Collectors.groupingBy(
                request -> request.getCreatedAt().toLocalDate(), // Assuming createdAt is a LocalDateTime
                Collectors.counting()
            ));
    }
}
