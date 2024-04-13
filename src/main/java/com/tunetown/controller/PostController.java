package com.tunetown.controller;

import com.tunetown.model.Comment;
import com.tunetown.model.Post;
import com.tunetown.model.Song;
import com.tunetown.service.PostService;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {
    @Resource
    PostService postService;
    @PostMapping("/create")
    public ResponseEntity<String> createPost(@RequestBody Post post){
        postService.createPost(post);
        return ResponseEntity.ok("Post added successfully");
    }

    @GetMapping
    public Map<String, Object> getAllPosts(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Post> postPage = postService.getAllPosts(pageRequest);
        return Map.of(
                "postList", postPage.getContent(),
                "currentPage", postPage.getNumber() + 1,
                "totalPages", postPage.getTotalPages(),
                "totalElement", postPage.getTotalElements()
        );
    }
    @GetMapping("/getByAuthorId")
    public Map<String, Object> getPostByAuthorId(@RequestParam int authorId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
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
    public ResponseEntity<String> likePost(@RequestParam int userId, @RequestParam int postId){
        if(postService.likePost(userId, postId) == 0){
            return ResponseEntity.ok("Post liked!");
        }
        else{
            return ResponseEntity.ok("Post unliked!");
        }
    }

    @GetMapping("/getById")
    public Post getPostById(@RequestParam int postId){
        Post post = postService.getPostById(postId);
        return post;
    }

    @GetMapping("/comment/getById")
    public Comment getCommentById(@RequestParam int commentId){
        Comment comment = postService.getCommentById(commentId);
        return comment;
    }
}
