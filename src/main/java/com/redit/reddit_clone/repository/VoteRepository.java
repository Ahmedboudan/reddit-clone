package com.redit.reddit_clone.repository;

import com.redit.reddit_clone.domain.Post;
import com.redit.reddit_clone.domain.User;
import com.redit.reddit_clone.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}
