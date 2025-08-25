package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.stream.Collectors; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpSession;
import com.example.demo.repository.*;
import com.example.demo.model.*;
import java.util.*;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JavaMailSender mailSender;
    
    // Show Home page
    @GetMapping("/")
    public String showHomeForm() {
        return "index";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalidate the session
        return "redirect:/login"; // Redirect to login page
    }
    @GetMapping("/about")
    public String showAboutForm(Model model) {
    	 long totalDonors = userRepository.count();
    	 long acceptedDonations = requestRepository.findByStatus("Accepted").size();
    	 model.addAttribute("totalDonors", totalDonors);
    	 model.addAttribute("acceptedDonations", acceptedDonations);
        return "about";
    }
    @GetMapping("/explore")
    public String showExploreForm() {
        return "explore";
    }
    

    // Show Registration form
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @Transactional
    @PostMapping("/register")
    public String handleRegistration(@RequestParam String name,
                                      @RequestParam String email,
                                      @RequestParam String password,
                                      @RequestParam String place,
                                      @RequestParam String phone,
                                      @RequestParam int age,
                                      @RequestParam String bloodType,
                                      Model model) {
        try {
        	if (userRepository.existsByEmail(email)) {
                model.addAttribute("error", "Email is already registered. Please use a different email.");
                return "register"; // Stay on the registration page
            }
            // Encode password before saving it
            String encodedPassword = passwordEncoder.encode(password);

            // Create and save the user
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);  // Save the encoded password
            user.setRole("donor");
            user.setPlace(place);
            user.setPhone(phone);
            user.setAge(age);
            user.setBloodType(bloodType);

            userRepository.save(user);
            model.addAttribute("message", "User registered successfully!");
            return "login"; // Redirect to login page

        } catch (Exception e) {
            model.addAttribute("error", "Error while registering user: " + e.getMessage());
            e.printStackTrace();
            return "register"; // Stay on the registration page if error occurs
        }
    }

    // Show Login form
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("otp", false);  // Default to false for normal login
        return "login";
    }

    // Handle Login form submission
    @PostMapping("/login")
    public String handleLogin(@RequestParam String email,
                              @RequestParam String password,
                              @RequestParam(required = false) boolean otpLogin,  // Handle OTP login
                              Model model,
                              HttpSession session) {
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
        System.out.println("OTP Login: " + otpLogin);

        // Step 1: Check if user exists
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            model.addAttribute("error", "No user found with this email.");
            return "login";  // Return to the login page if no user found
        }

        User user = userOptional.get();

        // Step 2: Handle OTP login (if OTP is selected)
        if (otpLogin) {
            // Generate OTP
            String generatedOtp = generateOtp();
            user.setOtp(generatedOtp);  // Store OTP in the user (optional, depending on your design)
            userRepository.save(user);

            sendOtpEmail(user.getEmail(), generatedOtp);  // Send OTP to the user's email

            // Store OTP in the session for verification
            session.setAttribute("generatedOtp", generatedOtp);

            model.addAttribute("message", "OTP sent to your email.");
            return "redirect:/verify-otp";  // Redirect to OTP verification page
        }

        // Step 3: Handle password-based login if OTP is not selected
        if (password != null && !password.isEmpty()) {
            // Ensure password is properly encoded in the database
            if (password.equals(user.getPassword())) {  // Use PasswordEncoder to verify
            	session.setAttribute("user", email);  // You can store the email or username
                
                session.setAttribute("donorId", user.getId());
                
                model.addAttribute("message", "Login successful.");
                return "redirect:/donor-dashboard";  // Redirect to dashboard after successful login
            } else {
                model.addAttribute("error", "Incorrect password.");
                return "login";  // Return to login page if password is incorrect
            }
        }

        // Step 4: Error if no password or OTP provided
        model.addAttribute("error", "Please provide either an OTP or password.");
        return "login";  // Stay on login page if neither OTP nor password is provided
    }

    // OTP Verification page
    @GetMapping("/verify-otp")
    public String showOtpForm() {
        return "otp-verification";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email, @RequestParam String otp, Model model, HttpSession session) {
        // Retrieve the generated OTP from the session
        String generatedOtp = (String) session.getAttribute("generatedOtp");
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.get();
        // If the OTP from session is null, something went wrong
        if (generatedOtp == null) {
            model.addAttribute("error", "No OTP generated or session expired.");
            return "login";  // Redirect to login page if no OTP exists
        }

        // Compare the OTP entered by the user with the generated OTP
        if (otp.equals(generatedOtp)) {
        	session.setAttribute("donorId", user.getId());
        	session.setAttribute("user", email);  // You can store the email or username
            model.addAttribute("message", "OTP successfully verified.");
            
            return "redirect:/donor-dashboard";  // Redirect to dashboard after successful OTP validation
        } else {
            model.addAttribute("error", "Invalid OTP.");
            return "otp-verification";  // Show OTP verification page again if OTP is incorrect
        }
    }

    // Forgot Password page
    @GetMapping("/forgot-password")
    public String showForgetPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String email, Model model) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Generate a password reset token
            String resetToken = generateOtp();  // You can use the same OTP logic for reset token

            // Save the reset token in the database
            user.setToken(resetToken);
            userRepository.save(user);

            // Send reset password email with token
            sendResetPasswordEmail(email, resetToken);

            model.addAttribute("message", "Password reset link sent to your email.");
        } else {
            model.addAttribute("error", "No user found with the provided email.");
        }
        return "forgot-password";
    }

    // Generate a random OTP
    private String generateOtp() {
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000); // Generate a 6-digit OTP
        return String.valueOf(otp);
    }

    // Send OTP to the user's email
    private void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@example.com");  // Set the "from" email address
        message.setTo(email);
        message.setSubject("OTP for Login");
        message.setText("Your OTP for login is: " + otp);
        mailSender.send(message);
    }

    // Send reset password email with token
    private void sendResetPasswordEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@example.com");  // Set the "from" email address
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("Click the link below to reset your password:\n" +
                        "http://localhost:8080/reset-password?token=" + token);
        mailSender.send(message);
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";  // Thymeleaf page for resetting the password
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam String password,
                                       @RequestParam String confirmPassword,
                                       @RequestParam String token, 
                                       Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "reset-password";  // Stay on the reset-password page if passwords do not match
        }

        if (token == null || token.isEmpty()) {
            model.addAttribute("error", "Invalid or expired reset token.");
            return "forgot-password";  // Redirect to forgot password page if token is invalid or not present
        }

        Optional<User> userOptional = userRepository.findByToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Encode the new password before saving it
            user.setPassword(password);
            user.setToken(null);  // Remove the reset token after password reset
            userRepository.saveAndFlush(user);  // Save and flush the changes

            model.addAttribute("message", "Password reset successfully. You can now login.");
            return "login";  // Redirect to login page after successful reset
        } else {
            model.addAttribute("error", "Invalid or expired reset token.");
            return "forgot-password";  // Redirect to forgot password page if token is invalid
        }
    }

    @GetMapping("/donor-dashboard")
    public String showDonorDashboard(Model model, HttpSession session) {
        // Get the donor's ID from the session
        Long donorId = (Long) session.getAttribute("donorId");

        // If no donor ID is present in the session, redirect to the login page with an error message
        if (donorId == null) {
            model.addAttribute("error", "You need to login first.");
            return "login";
        }

        // Retrieve the donor object from the repository based on the ID
        User donor = userRepository.findById(donorId).orElse(null);

        if (donor != null) {
            // Retrieve the requests associated with the donor
        	  List<Request> pendingRequests = requestRepository.findByDonorAndStatus(donor, "pending");
        	  List<Request> allRequests =requestRepository.findByDonorId(donorId);
        	  List<Request> acceptedRequests = allRequests.stream()
                      .filter(request -> "Accepted".equalsIgnoreCase(request.getStatus()))
                      .collect(Collectors.toList());

              List<Request> rejectedRequests = allRequests.stream()
                      .filter(request -> "Rejected".equalsIgnoreCase(request.getStatus()))
                      .collect(Collectors.toList());

              System.out.println("Pending Requests: " + pendingRequests);
              System.out.println("Accepted Requests: " + acceptedRequests);
              System.out.println("Rejected Requests: " + rejectedRequests);

            // Add the filtered list of requests and donor details to the model
        	  model.addAttribute("requests", pendingRequests);
        	  model.addAttribute("acceptedRequests", acceptedRequests);
        	  model.addAttribute("rejectedRequests", rejectedRequests);
            model.addAttribute("donor", donor); // Send donor details to the view
        } else {
            model.addAttribute("error", "Donor not found.");
        }

        return "donor-dashboard";
    }

    // This method handles the profile update POST request
    @PostMapping("/update-profile")
    public String updateProfile(@RequestParam("name") String name,
                                @RequestParam("email") String email,
                                @RequestParam("place") String place,
                                @RequestParam("phone") String phone,
                                @RequestParam("bloodType") String bloodType,
                                @RequestParam("age") int age,
                                HttpSession session, Model model) {

        Long donorId = (Long) session.getAttribute("donorId");
        if (donorId == null) {
            model.addAttribute("error", "You need to login first.");
            return "login";
        }

        User donor = userRepository.findById(donorId).orElse(null);

        if (donor != null) {
            donor.setName(name);
            donor.setEmail(email);
            donor.setPlace(place);
            donor.setPhone(phone);
            donor.setBloodType(bloodType);
            donor.setAge(age);

            // Save the updated donor information back to the database
            userRepository.save(donor);

            // Add the updated donor details to the model
            model.addAttribute("donor", donor);

            return "redirect:/donor-dashboard"; // Redirect to the dashboard after update
        } else {
            model.addAttribute("error", "Donor not found.");
            return "donor-dashboard";
        }
    }

    @PostMapping("/update-request")
    public String updateRequestStatus(@RequestParam("requestId") Long requestId,
                                      @RequestParam("status") String status,
                                      Model model) {
        // Find the request by ID
        Request request = requestRepository.findById(requestId).orElse(null);

        if (request != null) {
            // Update the status of the request
            request.setStatus(status);
            // Save the updated request
            requestRepository.save(request);

            // Retrieve receiver details
            Receiver receiver = request.getReceiver();

            // Send the appropriate email based on the status
            if ("Accepted".equalsIgnoreCase(status)) {
                sendAcceptanceEmail(receiver, request);
            } else if ("Rejected".equalsIgnoreCase(status)) {
                sendRejectionEmail(receiver, request);
            }


            // Add a success message to the model
            model.addAttribute("message", "Request status updated to " + status);
        } else {
            // If request not found, handle accordingly
            model.addAttribute("message", "Request not found.");
        }

        // Redirect back to the dashboard (or the page where requests are displayed)
        return "redirect:/donor-dashboard";
    }

    private void sendAcceptanceEmail(Receiver receiver, Request request) {
        // Create a SimpleMailMessage
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@example.com");
        message.setTo(receiver.getEmail());  // Receiver's email
        message.setSubject("Blood Donation Request Accepted");
        message.setText("Your blood donation request has been accepted by the donor. Here are the donor details:\n" +
                        "Donor Name: " + request.getDonor().getName() + "\n" +
                        "Donor Contact: " + request.getDonor().getPhone());
        // Send the email
        mailSender.send(message);
    }

    private void sendRejectionEmail(Receiver receiver, Request request) {
        // Create a SimpleMailMessage for rejection
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@example.com");
        message.setTo(receiver.getEmail());  // Receiver's email
        message.setSubject("Blood Donation Request Rejected");
        message.setText("Your blood donation request has been rejected by the donor. Donor: " + request.getDonor().getName());
        // Send the email
        mailSender.send(message);
    }

}


