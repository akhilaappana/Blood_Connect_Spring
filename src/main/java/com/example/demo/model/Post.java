package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;



@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    @Lob // This indicates the content should be stored as a TEXT type in the database
    private String content;
    private LocalDateTime createdAt;
    
    // This field will track the number of likes
    private int likesCount = 0;

    // Constructors, Getters, and Setters
    public Post() {
        this.createdAt = LocalDateTime.now();
    }

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
}
