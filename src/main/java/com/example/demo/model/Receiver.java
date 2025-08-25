package com.example.demo.model;

import jakarta.persistence.*;
@Entity
public class Receiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob  // Specifies that this field will be stored as a large object (TEXT/CLOB)
    @Column(columnDefinition = "LONGTEXT")  // Explicitly set the column type to LONGTEXT for MySQL
    private String proofFileBase64;  // This will store the Base64 encoded file as a string
    private String name;
    private String place;
    private String bloodTypeNeeded;  // Blood type the receiver needs
    private String phone;
    private String email;

    @Column(unique = true)  // Ensures receiverId is unique
    private Long receiverId;  // Optional: If you need an additional unique identifier

    @Lob
    private byte[] proofFile;  // Medical proof file for the receiver

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getBloodTypeNeeded() {
        return bloodTypeNeeded;
    }

    public void setBloodTypeNeeded(String bloodTypeNeeded) {
        this.bloodTypeNeeded = bloodTypeNeeded;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

   
    public byte[] getProofFile() {
        return proofFile;
    }

    public void setProofFile(byte[] proofFile) {
        this.proofFile = proofFile;
    }

	public Long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}
	 public String getProofFileBase64() {
	        return proofFileBase64;
	    }

	    public void setProofFileBase64(String proofFileBase64) {
	        this.proofFileBase64 = proofFileBase64;
	    }

		
    
}
