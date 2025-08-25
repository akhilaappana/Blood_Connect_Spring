package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String commenterName;
    private String content;
    private Long donorId;  // This will link the comment to the donor
    private Long postId;  // This links the comment to the post
    public Comment() {
        // Hibernate requires this constructor to create the object
    } private String username; // Add the username field

    // Other fields and methods...

    // Getter and setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

 // Constructor with parameters
    public Comment(Long donorId, Long postId, String content) {
        this.donorId = donorId;
        this.postId = postId;
        this.content = content;
    }
    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}
