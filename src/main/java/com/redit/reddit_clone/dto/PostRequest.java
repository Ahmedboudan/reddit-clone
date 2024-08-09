package com.redit.reddit_clone.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class PostRequest {
    private Long postId;
    private String postName;
    private String url;
    private String subredditName;
    private String description;
}
