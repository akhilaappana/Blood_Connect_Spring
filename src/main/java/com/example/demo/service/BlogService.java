package com.example.demo.service;

import com.example.demo.model.Comment;
import com.example.demo.model.Like;
import com.example.demo.model.Post;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    // Get all posts
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // Find post by ID
    public Post findPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    // Save a new post
    public void savePost(Post post) {
        postRepository.save(post);
    }

    // Save a comment
    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    // Save a like
    public void saveLike(Like like) {
        likeRepository.save(like);
    }

    // Get comments for a specific post
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    // Get likes for a specific post
    public List<Like> getLikesByPostId(Long postId) {
        return likeRepository.findByPostId(postId);
    }
}
