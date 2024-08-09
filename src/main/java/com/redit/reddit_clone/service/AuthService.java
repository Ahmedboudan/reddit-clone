package com.redit.reddit_clone.service;

import com.redit.reddit_clone.domain.NotificationEmail;
import com.redit.reddit_clone.domain.User;
import com.redit.reddit_clone.domain.VerificationToken;
import com.redit.reddit_clone.dto.AuthenticationResponse;
import com.redit.reddit_clone.dto.LoginRequest;
import com.redit.reddit_clone.dto.RefreshTokenRequest;
import com.redit.reddit_clone.dto.RegisterRequest;
import com.redit.reddit_clone.exceptions.SpringRedditException;
import com.redit.reddit_clone.repository.UserRepository;
import com.redit.reddit_clone.repository.VerificationTokenRepository;
import com.redit.reddit_clone.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;


    @Transactional
    public void signup(RegisterRequest registerRequest) {
        log.info("Signing up user");
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setCreated(Instant.now());
        user.setEnabled(false);
        userRepository.save(user);

        String token = generateVerificationToken(user);
        mailService.sendMail(
                new NotificationEmail(
                        "Please activate your account",
                        user.getEmail(),
                        "Thank you for signing up to Spring Reddit " +
                                "please click on the below url to activate your account : " +
                                "http://localhost:8080/api/auth/accountVerification/" + token

                )
        );
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken1 = new VerificationToken();
        verificationToken1.setToken(token);
        verificationToken1.setUser(user);
        verificationTokenRepository.save(verificationToken1);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new SpringRedditException("Invalid token"));
        fetchUserAndEnable(verificationToken.get());
    }

    @Transactional
    public void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new SpringRedditException("User not found with username " + username)
        );
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest request) {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .expiresAt(Instant.now().minusMillis(jwtProvider.getJwtExpirationTime()))
                .username(request.getUsername())
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .build();
    }

    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new SpringRedditException("User not found with username - " + principal.getUsername()));
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        refreshTokenService.validateRefreshToken(request.getRefreshToken());
        String token = jwtProvider.generateTokenWithUsername(request.getUsername());
        return AuthenticationResponse.builder()
                .refreshToken(request.getRefreshToken())
                .authenticationToken(token)
                .username(request.getUsername())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationTime()))
                .build();
    }
}
