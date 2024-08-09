//package com.redit.reddit_clone.mapper;
//
//import com.redit.reddit_clone.domain.Post;
//import com.redit.reddit_clone.domain.Subreddit;
//import com.redit.reddit_clone.domain.User;
//import com.redit.reddit_clone.dto.PostRequest;
//import com.redit.reddit_clone.dto.PostResponse;
//import org.springframework.stereotype.Component;
//
//import java.time.Instant;
//
//@Component
//public class PostMapperImpl implements PostMapper {
//
//    @Override
//    public Post map(PostRequest postRequest, Subreddit subreddit, User user) {
//        if (postRequest == null && subreddit == null && user == null) {
//            return null;
//        }
//
//        Post post = new Post();
//
//        if (postRequest != null) {
//            post.setDescription(postRequest.getDescription());
//            post.setPostName(postRequest.getPostName());
//            post.setUrl(postRequest.getUrl());
//        }
//
//        post.setCreatedDate(Instant.now());
//
//        if (subreddit != null) {
//            post.setSubreddit(subreddit);
//        }
//
//        if (user != null) {
//            post.setUser(user);
//        }
//
//        return post;
//    }
//
//    @Override
//    public PostResponse mapToDto(Post post) {
//        if (post == null) {
//            return null;
//        }
//
//        PostResponse postResponse = new PostResponse();
//
//        postResponse.setId(post.getPostId());
//        postResponse.setPostName(post.getPostName());
//        postResponse.setUrl(post.getUrl());
//        postResponse.setDescription(post.getDescription());
//
//        if (post.getUser() != null) {
//            postResponse.setUserName(post.getUser().getUsername());
//        }
//
//        if (post.getSubreddit() != null) {
//            postResponse.setSubredditName(post.getSubreddit().getName());
//        }
//
//        return postResponse;
//    }
//}
