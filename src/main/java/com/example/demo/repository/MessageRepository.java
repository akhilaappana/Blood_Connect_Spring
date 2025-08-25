package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.*;
import java.util.*;
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdOrReceiverId(Long senderId, Long receiverId);
}
