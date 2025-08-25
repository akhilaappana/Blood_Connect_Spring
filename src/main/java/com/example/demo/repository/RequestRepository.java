package com.example.demo.repository;

import com.example.demo.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.*;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByDonorId(Long donorId);
    List<Request> findByDonor(User donor);
    List<Request> findByStatus(String status);
    List<Request> findByDonorAndStatus(User donor, String status);
    }

