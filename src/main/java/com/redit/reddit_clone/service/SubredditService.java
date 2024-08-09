package com.redit.reddit_clone.service;


import com.redit.reddit_clone.domain.Subreddit;
import com.redit.reddit_clone.domain.User;
import com.redit.reddit_clone.dto.SubredditDto;
import com.redit.reddit_clone.exceptions.SpringRedditException;
import com.redit.reddit_clone.mapper.SubredditMapper;
import com.redit.reddit_clone.repository.SubredditRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static java.util.stream.Collectors.toList;


@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {
    private final SubredditRepository subredditRepository;
    private final SubredditMapper subredditMapper;
    private final AuthService authService;

    @Transactional
    public SubredditDto save(SubredditDto subredditDto) {
        Subreddit subreddit = subredditMapper.mapDtoToSubreddit(subredditDto);
        subreddit.setCreatedDate(Instant.now());
        subreddit.setUser(authService.getCurrentUser());
        subreddit = subredditRepository.save(subreddit);
        subredditDto.setId(subreddit.getId());
        return subredditDto;
    }

    @Transactional
    public List<SubredditDto> getAll() {
        log.info("Fetching all subreddits");
        List<Subreddit> subreddits = subredditRepository.findAll();
        log.info("Found {} subreddits", subreddits.size());
        return subreddits.stream()
                .map(subredditMapper::mapSubredditToDto)
                .collect(toList());
    }

    public SubredditDto getSubreddit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(()-> new SpringRedditException("No subreddit found with given id "+id));
        return subredditMapper.mapSubredditToDto(subreddit);
    }
}
