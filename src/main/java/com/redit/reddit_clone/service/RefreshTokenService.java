package com.redit.reddit_clone.service;

import com.redit.reddit_clone.domain.RefreshToken;
import com.redit.reddit_clone.exceptions.SpringRedditException;
import com.redit.reddit_clone.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken generateRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedDate(Instant.now());

        return refreshTokenRepository.save(refreshToken);
    }

    public void validateRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
                .orElseThrow(
                        () -> new SpringRedditException("Invalid refresh token !")
                );
    }
    public void deleterefreshToken(String token){
        refreshTokenRepository.deleteByToken(token);
    }
}
