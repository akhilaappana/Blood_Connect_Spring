package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.model.Receiver;
import com.example.demo.model.Request;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.mail.MessagingException;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ReceiverRepository;
import com.example.demo.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.core.io.ByteArrayResource;
import jakarta.servlet.http.HttpSession;
import java.util.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
@Controller
public class ReciverController {
    
    @Autowired
    private ReceiverRepository receiverRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender javaMailSender;
    
    @Autowired
    private UserRepository donorRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private HttpSession session;

    // Helper method to generate OTP
    private String generateOtp(int length) {
        StringBuilder otp = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10)); // Add a random digit (0-9)
        }
        return otp.toString();  // Return the generated OTP as a string
    }

    // Display receiver registration form
    @GetMapping("/register-receiver")
    public String showReceiverRegistrationForm(Model model) {
        String receiverId = generateOtp(6);  // 6-digit OTP
        model.addAttribute("receiverId", receiverId);  // Pass OTP to the model
        return "register-receiver";  // Return the Thymeleaf template
    }

    // Handle receiver registration
    @PostMapping("/register-receiver")
    public String handleReceiverRegistration(@RequestParam Long receiverId,
                                             @RequestParam String name,
                                             @RequestParam String place,
                                             @RequestParam String bloodTypeNeeded,
                                             @RequestParam String phone,
                                             @RequestParam String email,
                                             @RequestParam MultipartFile proofFile,
                                             Model model) {
        try {
            // Create the receiver object
            Receiver receiver = new Receiver();
            receiver.setReceiverId(receiverId);  
            receiver.setName(name);
            receiver.setPlace(place);
            receiver.setBloodTypeNeeded(bloodTypeNeeded);
            receiver.setPhone(phone);
            receiver.setEmail(email);
            
            
            // Convert the MultipartFile to byte[] (for proof file)
            byte[] proofFileBytes = proofFile.getBytes();
            receiver.setProofFile(proofFileBytes);  // Store tehe proof file as byte array in the database
            String proofFileBase64 = encodeToBase64(proofFileBytes);
            receiver.setProofFileBase64(proofFileBase64);

            // Save the receiver entity in the database
            receiverRepository.save(receiver);
            session.setAttribute("receiverId", receiver.getReceiverId());
            model.addAttribute("message", "Receiver registered successfully!");
            // After registration, redirect to the login page
            return "redirect:/donor-list";  // Redirect to the login page after successful registration
        } catch (IOException e) {
            model.addAttribute("error", "Error uploading proof file.");
            return "register-receiver";  // Stay on the registration page if an error occurs
        }
    }

    // Show donor list for the receiver
    @GetMapping("/donor-list")
    public String showDonorList(Model model) {
        // Get receiverId from session
        Long receiverId = (Long) session.getAttribute("receiverId");
        System.out.println("Receiver ID in session: " + receiverId);
        if (receiverId == null) {
            model.addAttribute("error", "Receiver not logged in. Please log in first.");
            return "redirect:/login-receiver"; // Redirect to login if receiverId is not found in session
        }
        Receiver receiver = receiverRepository.findByReceiverId(receiverId);
 

        String receiverBloodType = receiver.getBloodTypeNeeded();
        String receiverLocation = receiver.getPlace();  // assuming location is a field in receiver

        // Get the list of all donors from the database
        List<User> allDonors = donorRepository.findAll();

        // Filter the donors by blood type or location (same blood type or same area)
        List<User> filteredDonors = allDonors.stream()
                .filter(donor -> donor.getBloodType().equalsIgnoreCase(receiverBloodType) || donor.getPlace().equalsIgnoreCase(receiverLocation))
                .collect(Collectors.toList());

        model.addAttribute("isRequestPage", false);
        model.addAttribute("donors", filteredDonors);
        model.addAttribute("receiverId", receiverId);  // Add receiverId to the model

        return "donor-list";
    }

    @PostMapping("/request-donor")
    public String sendRequestToDonor(@RequestParam String donorEmail,
                                     @RequestParam Long receiverId,
                                     Model model) {
        Optional<User> donorOptional = donorRepository.findByEmail(donorEmail);
        Receiver receiver = receiverRepository.findByReceiverId(receiverId);
        model.addAttribute("isRequestPage", true);
        if (receiver == null) {
            model.addAttribute("error", "Receiver not found for ID: " + receiverId);
            return "donor-list";
        }

        if (donorOptional.isEmpty()) {
            model.addAttribute("error", "Donor not found for email: " + donorEmail);
            return "donor-list";
        }

        User donor = donorOptional.get();

        try {
            // Create a request and save it to the database
            Request request = new Request();
            request.setReceiver(receiver);
            request.setDonor(donor);
            request.setStatus("Pending");
            requestRepository.save(request);

            // Prepare the email with MimeMessage
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true);  // true to support multipart (attachments)

            messageHelper.setFrom("your-email@example.com");  // Sender email
            messageHelper.setTo(donorEmail);  // Recipient email
            messageHelper.setSubject("Blood Donation Request");
            String emailContent = 
                    "Dear " + donor.getName() + ",<br><br>" +
                    "You have received a new blood donation request:<br><br>" +
                    "Receiver Name: " + receiver.getName() + "<br>" +
                    "Blood Type Needed: " + receiver.getBloodTypeNeeded() + "<br>" +
                    "Place: " + receiver.getPlace() + "<br>" +
                    "Contact: <a href=\"tel:" + receiver.getPhone() + "\">" + receiver.getPhone() + "</a><br><br>" +
                    "Please find the medical proof attached.<br><br>" +
                    "Thank you for your support!<br>";

                messageHelper.setText(emailContent, true);  // Set true for HTML content


            // Attach the proof file if available
            byte[] proofFileBytes = receiver.getProofFile();
            if (proofFileBytes != null && proofFileBytes.length > 0) {
                ByteArrayResource resource = new ByteArrayResource(proofFileBytes);
                messageHelper.addAttachment("ProofFile.jpg", resource);  // Adjust the file name and extension if needed
            }

            // Send the email
            javaMailSender.send(mailMessage);

            model.addAttribute("message", "Request sent successfully to " + donor.getName());
        } catch (Exception e) {
            model.addAttribute("error", "Request created, but failed to send email: " + e.getMessage());
            return "donor-list";
        }

        return "donor-list";
    }





    // Helper method to encode file to Base64
    public String encodeToBase64(byte[] fileBytes) {
        return Base64.getEncoder().encodeToString(fileBytes);
    }

    // Show receiver's proof file (image)
    @GetMapping("/view-proof/{receiverId}")
    public ResponseEntity<byte[]> viewProofFile(@PathVariable Long receiverId) {
        Optional<Receiver> receiverOptional = receiverRepository.findById(receiverId);

        if (receiverOptional.isPresent()) {
            Receiver receiver = receiverOptional.get();
            byte[] proofFileBytes = receiver.getProofFile();
            
            if (proofFileBytes != null && proofFileBytes.length > 0) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);
                return new ResponseEntity<>(proofFileBytes, headers, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // No file found for the receiver
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Receiver not found
        }
    }

   
}
