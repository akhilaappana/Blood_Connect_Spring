package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name="plike")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;  // This links the like to the post
  
    private Long donorId; // This links the like to the donor
    private String username; // Add the username field

    // Other fields and methods...

    // Getter and setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    // Constructor that accepts both postId and donorId
    public Like(Long donorId, Long postId) {
        this.donorId = donorId;
        this.postId = postId;
    }
    public Like() {
    }
    // Constructor
    public Like(Long postId) {
        this.postId = postId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
    public Long getDonorId() {
        return donorId;
    }
    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }
}
