package com.redit.reddit_clone.repository;

import com.redit.reddit_clone.domain.Comment;
import com.redit.reddit_clone.domain.Post;
import com.redit.reddit_clone.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
}
