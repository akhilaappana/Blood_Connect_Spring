package com.example.demo.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {
    @Id
    private Long id;
    private String content;
    private LocalDateTime timestamp;
    
    @ManyToOne
    private User sender;
    
    @ManyToOne
    private User receiver;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

    // Constructors, Getters, Setters
}
