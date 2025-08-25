package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.repository.*;
import com.example.demo.model.*;
import java.time.LocalDateTime;
import java.util.*;
@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    // Send Message
    public void sendMessage(Message message) {
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
    }

    // Get Messages between Users
    public List<Message> getMessages(Long userId1, Long userId2) {
        return messageRepository.findBySenderIdOrReceiverId(userId1, userId2);
    }
}
