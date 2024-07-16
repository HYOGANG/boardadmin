package com.boardadmin.board.controller;

import com.boardadmin.board.model.Post;
import com.boardadmin.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/board/{boardId}")
    public String getPostsByBoardId(@PathVariable Long boardId, @RequestParam(defaultValue = "1") int page, Model model) {
        List<Post> posts = postService.getPostsByBoardId(boardId);
        model.addAttribute("posts", posts);
        model.addAttribute("boardId", boardId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", 1); // 임시로 1페이지로 설정. 실제로는 페이지네이션 로직을 추가하세요.
        return "posts/list";
    }

    @GetMapping("/new")
    public String createPostForm(@RequestParam Long boardId, Model model) {
        Post post = new Post();
        post.setBoardId(boardId);
        model.addAttribute("post", post);
        model.addAttribute("boardId", boardId);
        return "posts/create";
    }

    @PostMapping
    public String createPost(@ModelAttribute Post post) {
        postService.createPost(post);
        return "redirect:/posts/board/" + post.getBoardId();
    }

    @GetMapping("/edit/{id}")
    public String editPostForm(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
        model.addAttribute("post", post);
        return "posts/edit";
    }

    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post post) {
        postService.updatePost(id, post);
        return "redirect:/posts/board/" + post.getBoardId();
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        Long boardId = postService.getPostById(id).map(Post::getBoardId).orElse(null);
        postService.deletePost(id);
        return "redirect:/posts/board/" + (boardId != null ? boardId : "");
    }
}
