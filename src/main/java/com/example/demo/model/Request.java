package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)  // Ensure receiver is always present in the Request
    private Receiver receiver;

    @ManyToOne
    @JoinColumn(name = "donor_id", nullable = false)  // Ensure donor is always present in the Request
    private User donor;

    private String status;  // Pending, Accepted, Rejected
    @Lob
    private byte[] proofFile;  // Store the proof file here (e.g., as a byte array)
    private LocalDateTime createdAt; // Example field

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Getter and Setter for proofFile
    public byte[] getProofFile() {
        return proofFile;
    }

    public void setProofFile(byte[] proofFile) {
        this.proofFile = proofFile;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public User getDonor() {
        return donor;
    }

    public void setDonor(User donor) {
        this.donor = donor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
