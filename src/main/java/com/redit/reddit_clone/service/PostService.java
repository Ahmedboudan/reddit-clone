package com.redit.reddit_clone.service;

import com.redit.reddit_clone.domain.Post;
import com.redit.reddit_clone.domain.Subreddit;
import com.redit.reddit_clone.domain.User;
import com.redit.reddit_clone.dto.PostRequest;
import com.redit.reddit_clone.dto.PostResponse;
import com.redit.reddit_clone.exceptions.SpringRedditException;
import com.redit.reddit_clone.mapper.PostMapper;
import com.redit.reddit_clone.repository.PostRepository;
import com.redit.reddit_clone.repository.SubredditRepository;
import com.redit.reddit_clone.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final AuthService authService;
    private final SubredditRepository subredditRepository;
    private final UserRepository userRepository;

    public Post save(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SpringRedditException(postRequest.getSubredditName()));
        User currentUser = authService.getCurrentUser();
        log.info("user {}",currentUser);
        Post post = postMapper.map(postRequest, subreddit, currentUser);
        log.info("post {}",post.toString());
        return postRepository.save(post);
    }

    @Transactional
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException("Post not found with id " + id));
        return postMapper.mapToDto(post);
    }

    @Transactional
    public List<PostResponse> getAll() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new SpringRedditException("User not found with username " + username));
        List<Post> posts = postRepository.findByUser(user);
        return posts.stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId)
                .orElseThrow(() -> new SpringRedditException("Subreddit not found with id " + subredditId));
        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        return posts.stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
