package com.redit.reddit_clone.controller;

import com.redit.reddit_clone.dto.SubredditDto;
import com.redit.reddit_clone.service.SubredditService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subreddit")
@AllArgsConstructor
@Slf4j
public class SubredditController {
    private final SubredditService subredditService;

    @PostMapping
    public ResponseEntity<SubredditDto> createSubreddit(@RequestBody SubredditDto subredditDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subredditService.save(subredditDto));
    }

    @GetMapping
    public ResponseEntity<List<SubredditDto>> getAll(){
        log.info("getAll called");
        List<SubredditDto> subreddits = subredditService.getAll();
        log.info("Found {} subreddits", subreddits.size());
        return ResponseEntity.ok(subreddits);
    }
    @GetMapping("/test")
    public List<String> test(){
        return List.of("test","test");
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubredditDto> getSubreddit(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(subredditService.getSubreddit(id));
    }
}
