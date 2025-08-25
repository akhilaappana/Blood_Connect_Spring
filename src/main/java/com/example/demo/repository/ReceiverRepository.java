package com.example.demo.repository;

import com.example.demo.model.Receiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiverRepository extends JpaRepository<Receiver, Long> {
    Receiver findByReceiverId(Long receiverId);
    Receiver findByEmail(String email);
}

