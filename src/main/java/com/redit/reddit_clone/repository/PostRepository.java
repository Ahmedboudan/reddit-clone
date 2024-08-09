package com.redit.reddit_clone.repository;

import com.redit.reddit_clone.domain.Post;
import com.redit.reddit_clone.domain.Subreddit;
import com.redit.reddit_clone.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findByUser(User user);

    List<Post> findAllBySubreddit(Subreddit subreddit);
}
