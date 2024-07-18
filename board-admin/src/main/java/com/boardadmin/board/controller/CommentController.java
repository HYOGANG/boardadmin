package com.boardadmin.board.controller;

import com.boardadmin.board.model.Comment;
import com.boardadmin.board.model.Post;
import com.boardadmin.board.service.CommentService;
import com.boardadmin.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @GetMapping
    public String getAllComments(Model model) {
        List<Comment> comments = commentService.getAllComments();
        model.addAttribute("comments", comments);
        return "comments/list";
    }

    @GetMapping("/post/{postId}")
    public String getCommentsByPostId(@PathVariable Long postId, Model model) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        model.addAttribute("comments", comments);
        model.addAttribute("postId", postId);
        return "comments/list";
    }

    @PostMapping("/post/{postId}")
    public String createComment(@PathVariable Long postId, @RequestParam String content, @RequestParam Long authorId) {
        Post post = postService.getPostById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(content);
        comment.setAuthorId(authorId); // 설정
        commentService.createComment(comment);
        return "redirect:/posts/view/" + postId;
    }

    @DeleteMapping("/delete/{id}")
    public String deleteComment(@PathVariable Long id, @RequestParam Long postId) {
        commentService.deleteComment(id);
        return "redirect:/posts/view/" + postId;
    }

    // 댓글 수정 폼으로 이동하는 메서드
    @GetMapping("/edit/{id}")
    public String editCommentForm(@PathVariable Long id, Model model) {
        Comment comment = commentService.getCommentById(id).orElseThrow(() -> new IllegalArgumentException("Invalid comment Id:" + id));
        model.addAttribute("comment", comment);
        return "comments/edit"; // comments/edit.html 템플릿으로 이동
    }

    // 댓글 수정 요청을 처리하는 메서드
    @PostMapping("/edit/{id}")
    public String updateComment(@PathVariable Long id, @RequestParam String content, @RequestParam Long postId) {
        Comment existingComment = commentService.getCommentById(id).orElseThrow(() -> new IllegalArgumentException("Invalid comment Id:" + id));
        existingComment.setContent(content);
        commentService.updateComment(existingComment);
        return "redirect:/posts/view/" + postId;
    }
}
