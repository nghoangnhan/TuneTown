package com.tunetown.service;

import com.tunetown.model.*;
import com.tunetown.repository.*;
import com.tunetown.service.jwt.JwtService;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class PostService {
    @Resource
    UserRepository userRepository;
    @Resource
    UserService userService;
    @Resource
    SongRepository songRepository;
    @Resource
    PlaylistRepository playlistRepository;
    @Resource
    PostRepository postRepository;
    @Resource
    JwtService jwtService;
    @Resource
    CommentRepository commentRepository;


    public void createPost(Post post){
        Optional<User> optionalUser = userRepository.findById(post.getAuthor().getId());
        if(!optionalUser.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id = " + optionalUser.get().getId() + " does not exists!");
        }

        if(post.getSong() != null)
        {
            Optional<Song> optionalSong = songRepository.findById(post.getSong().getId());
            if(!optionalSong.isPresent()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song with id = " + optionalSong.get().getId() + " does not exists!");
            }
        }

        if(post.getPlaylist() != null){
            Optional<Playlist> optionalPlaylist = playlistRepository.findById(post.getPlaylist().getId());
            if(!optionalPlaylist.isPresent()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist with id = " + optionalPlaylist.get().getId() + " does not exists!");
            }
        }
        post.setPostTime(LocalDateTime.now());
        postRepository.save(post);
    }

    public Page<Post> getAllPosts(Pageable pageable){
        Page<Post> listPost =  postRepository.getAllPosts(pageable);
        return listPost;
    }

    public Page<Post> getPostByAuthorId(int authorId, Pageable pageable){
        Page<Post> listPost =  postRepository.getPostByAuthorId(authorId, pageable);
        return listPost;
    }

    @Transactional
    public boolean updatePost(Post post, String accessToken){
        Optional<Post> optionalPost = postRepository.findById(post.getId());
        if(!optionalPost.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id = " + post.getId() + " does not exists!");
        }

        Post postUpdate = optionalPost.get();
        User user = userRepository.findById(post.getAuthor().getId()).get();

        String token = accessToken.substring(6, accessToken.length());
        String userEmail = jwtService.extractUserEmail(token.toString());
        User currentUser = userService.getActiveUserByEmail(userEmail);

        boolean isAuthor = false;

        if(userEmail.equals(user.getEmail()) || currentUser.getRole().toUpperCase().equals("ADMIN")){
            isAuthor = true;
        }

        if(isAuthor){
            postUpdate.setContent(post.getContent());
            postUpdate.setPostTime(LocalDateTime.now());
            postUpdate.setSong(post.getSong());
            postUpdate.setPlaylist(post.getPlaylist());
            postUpdate.setMp3Link((postUpdate.getMp3Link()));
            postRepository.save(postUpdate);
            return true;
        }
        else{
            return false;
        }
    }

    public boolean deletePost(int postId, String accessToken){
        Optional<Post> optionalPost = postRepository.findById(postId);
        if(!optionalPost.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id = " + postId + " does not exists!");
        }

        Post post = optionalPost.get();
        User user = userRepository.findById(post.getAuthor().getId()).get();

        String token = accessToken.substring(6, accessToken.length());
        String userEmail = jwtService.extractUserEmail(token.toString());
        User currentUser = userService.getActiveUserByEmail(userEmail);

        boolean isAuthor = false;

        if(userEmail.equals(user.getEmail()) || currentUser.getRole().toUpperCase().equals("ADMIN")){
            isAuthor = true;
        }

        if(isAuthor){
            postRepository.deleteById(postId);
            for(Comment comment: post.getComments()){
                commentRepository.deleteById(comment.getId());
                for(Comment reply: comment.getReply()){
                    log.info("DELETE REPLY");
                    commentRepository.delete(reply);
                }
            }
            return true;
        }
        else{
            return false;
        }
    }

    public void addComment(int postId, Comment comment){
        Optional<Post> optionalPost = postRepository.findById(postId);
        if(!optionalPost.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id = " + postId + " does not exists!");
        }

        Post post = optionalPost.get();
        comment.setCommentDate(LocalDateTime.now());
        commentRepository.save(comment);
        post.getComments().add(comment);
        postRepository.save(post);
    }

    public void addReply(int postId, int commentId, Comment reply){
        Optional<Post> optionalPost = postRepository.findById(postId);
        if(!optionalPost.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id = " + postId + " does not exists!");
        }

        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if(!optionalComment.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment with id = " + commentId + " does not exists!");
        }

        Comment comment = optionalComment.get();

        reply.setCommentDate(LocalDateTime.now());
        commentRepository.save(reply);

        comment.getReply().add(reply);
        commentRepository.save(comment);
    }

    public int likePost(int userId, int postId){
        Optional<User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id = " + userId + " does not exists!");
        }

        Optional<Post> optionalPost = postRepository.findById(postId);
        if(!optionalPost.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id = " + postId + " does not exists!");
        }

        User user = optionalUser.get();
        Post post = optionalPost.get();

        if(!post.getLikes().contains(user)){
            post.getLikes().add(user);
            postRepository.save(post);
            return 0;
        }
        else{
            post.getLikes().remove(user);
            postRepository.save(post);
            return 1;
        }
    }

    public Post getPostById(int postId){
        Optional<Post> optionalPost = postRepository.findById(postId);
        if(!optionalPost.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id = " + postId + " does not exists!");
        }
        Post post = optionalPost.get();
        return post;
    }

    public Comment getCommentById(int commentId){
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if(!optionalComment.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment with id = " + commentId + " does not exists!");
        }
        Comment comment = optionalComment.get();
        return comment;
    }
}
