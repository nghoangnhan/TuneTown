package com.tunetown.controller;

import com.tunetown.model.Comment;
import com.tunetown.model.Post;
import com.tunetown.model.User;
import com.tunetown.service.PostService;
import com.tunetown.service.UserService;
import com.tunetown.service.jwt.JwtService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {
    @Resource
    PostService postService;
    @Resource
    JwtService jwtService;
    @Resource
    UserService userService;
    @PostMapping("/create")
    public ResponseEntity<String> createPost(@RequestBody Post post){
        postService.createPost(post);
        return ResponseEntity.ok("Post added successfully");
    }

    @GetMapping
    public Map<String, Object> getAllPosts(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestHeader("Authorization") String accessToken){
        String email = jwtService.extractUserEmail(accessToken.substring(6));
        User user = userService.getActiveUserByEmail(email);

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Post> postPage = postService.getAllPosts(user.getId(), pageRequest);
        return Map.of(
                "postList", postPage.getContent(),
                "currentPage", postPage.getNumber() + 1,
                "totalPages", postPage.getTotalPages(),
                "totalElement", postPage.getTotalElements()
        );
    }

    @GetMapping("/getPostsByAdmin")
    public Map<String, Object> getPostsByAdmin(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Post> postPage = postService.getPostsByAdmin(pageable);
        return Map.of(
                "currentPage", postPage.getNumber() + 1,
                "postList", postPage.getContent(),
                "totalPages", postPage.getTotalPages(),
                "totalElement", postPage.getTotalElements()
        );
    }

    @GetMapping("/getByAuthorId")
    public Map<String, Object> getPostByAuthorId(@RequestParam UUID authorId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Post> postPage = postService.getPostByAuthorId(authorId, pageRequest);
        return Map.of(
                "postList", postPage.getContent(),
                "currentPage", postPage.getNumber() + 1,
                "totalPages", postPage.getTotalPages(),
                "totalElement", postPage.getTotalElements()
        );
    }

    @PutMapping
    public ResponseEntity<String> updatePost(@RequestBody Post post, @RequestHeader("Authorization") String accessToken){
        if(postService.updatePost(post, accessToken)){
            return ResponseEntity.ok("Post updated!");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not the author!");
    }

    @DeleteMapping
    public ResponseEntity<String> deletePost(@RequestParam int postId, @RequestHeader("Authorization") String accessToken){
        if(postService.deletePost(postId, accessToken)){
            return ResponseEntity.ok("Post deleted!");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not the author!");
    }

    @PostMapping("/addComment")
    public ResponseEntity<String> addComment(@RequestParam int postId, @RequestBody Comment comment){
        postService.addComment(postId, comment);
        return ResponseEntity.ok("Comment added!");
    }

    @PostMapping("/addReply")
    public ResponseEntity<String> addReply(@RequestParam int postId, @RequestParam int commentId, @RequestBody Comment reply){
        postService.addReply(postId, commentId, reply);
        return ResponseEntity.ok("Reply added!");
    }

    @PostMapping("/likePost")
    public ResponseEntity<String> likePost(@RequestParam UUID userId, @RequestParam int postId){
        if(postService.likePost(userId, postId) == 0){
            return ResponseEntity.ok("Post liked!");
        }
        else{
            return ResponseEntity.ok("Post unliked!");
        }
    }

    @GetMapping("/getById")
    public Post getPostById(@RequestParam int postId){
        return postService.getPostById(postId);
    }

    @GetMapping("/comment/getById")
    public Comment getCommentById(@RequestParam int commentId){
        return postService.getCommentById(commentId);
    }
}
