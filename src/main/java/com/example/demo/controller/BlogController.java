package com.example.demo.controller;

import com.example.demo.model.Post;
import com.example.demo.model.Comment;
import com.example.demo.model.Like;
import com.example.demo.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.repository.*;
import jakarta.servlet.http.HttpSession;  // Import HttpSession
import java.util.Optional;  // Import Optional

// Rest of your code

@Controller
public class BlogController {

    @Autowired
    private BlogService blogService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private LikeRepository likeRepository;

    // Show all posts in the blog
    @GetMapping("/blogs")
    public String showBlogs(Model model, HttpSession session) {
        model.addAttribute("posts", blogService.getAllPosts());
        // Add login check
        String loggedInUser = (String) session.getAttribute("user");
        model.addAttribute("isLoggedIn", loggedInUser != null);
        return "blogs";  // Thymeleaf template for displaying the list of posts
    }

    

    @GetMapping("/create-post")
    public String showCreate(Model model) {
        model.addAttribute("posts", blogService.getAllPosts());
        return "create-post";  // Thymeleaf template for displaying the list of posts
    }

    // Add a new post
    @PostMapping("/create-post")
    public String createPost(@RequestParam String title, @RequestParam String content) {
        Post post = new Post(title, content);
        blogService.savePost(post);
        return "redirect:/blogs";
    }

 // Show Post with Comments and Likes
    @GetMapping("/post/{id}")
    public String showPost(@PathVariable Long id, Model model, HttpSession session) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (!postOptional.isPresent()) {
            model.addAttribute("error", "Post not found");
            return "error";  // Error page if post not found
        }

        // Fetch the post details
        Post post = postOptional.get();
        model.addAttribute("post", post);
        model.addAttribute("comments", commentRepository.findByPostId(id));
        model.addAttribute("likes", likeRepository.findByPostId(id));

        // Check if user is logged in (i.e., is a donor)
        String loggedInUser = (String) session.getAttribute("user");
        if (loggedInUser != null) {
            model.addAttribute("isLoggedIn", true);  // Indicates that the user can interact
        } else {
            model.addAttribute("isLoggedIn", false);
        }

        return "post";  // Thymeleaf template to display the post details along with comments and likes
    }


 // Like a post
    @PostMapping("/post/{id}/like")
    public String addLike(@PathVariable Long id, HttpSession session) {
        // Get the logged-in user's username
        String loggedInUser = (String) session.getAttribute("user");
        
        if (loggedInUser != null) {
            // Check if the user has already liked the post
            Optional<Like> existingLike = likeRepository.findByPostIdAndUsername(id, loggedInUser);
            
            if (existingLike.isPresent()) {
                // If the like already exists, return to the post without adding another like
                return "redirect:/post/" + id + "?error=alreadyLiked";  // Add error query param to show a message
            }
            
            // If not already liked, create and save the like
            Like like = new Like();
            like.setPostId(id);
            like.setUsername(loggedInUser);  // Store the username of the liker (logged-in user)
            likeRepository.save(like);
            
            return "redirect:/post/" + id;
        }
        
        // Redirect to login if user is not authenticated
        return "redirect:/login"; 
    }


 // Add a new comment
    @PostMapping("/post/{id}/comment")
    public String addComment(@PathVariable Long id, @RequestParam String commentContent, HttpSession session) {
        String loggedInUser = (String) session.getAttribute("user");
        if (loggedInUser != null) {
            Comment comment = new Comment();
            comment.setContent(commentContent);
            comment.setPostId(id);
            comment.setUsername(loggedInUser);  // Store the username of the commenter (logged-in donor)
            commentRepository.save(comment);
            return "redirect:/post/" + id;
        }
        return "redirect:/login";  // Redirect to login if user is not authenticated
    }
}
