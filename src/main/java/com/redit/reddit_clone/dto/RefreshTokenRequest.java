package com.redit.reddit_clone.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RefreshTokenRequest {
    private String username;
    @NotBlank
    private String refreshToken;
}
