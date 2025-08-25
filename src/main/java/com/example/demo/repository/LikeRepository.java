package com.example.demo.repository;

import com.example.demo.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface LikeRepository extends JpaRepository<Like, Long> {
	Optional<Like> findByPostIdAndUsername(Long postId, String username);
    List<Like> findByPostId(Long postId);
}
