package com.redit.reddit_clone.controller;


import com.redit.reddit_clone.domain.User;
import com.redit.reddit_clone.dto.AuthenticationResponse;
import com.redit.reddit_clone.dto.LoginRequest;
import com.redit.reddit_clone.dto.RefreshTokenRequest;
import com.redit.reddit_clone.dto.RegisterRequest;
import com.redit.reddit_clone.service.AuthService;
import com.redit.reddit_clone.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody RegisterRequest registerRequest) {
        log.info("Signing up from controller");
        authService.signup(registerRequest);
        return new ResponseEntity("User registration successful", OK);
    }

    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account activated successfully", OK);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/authenticated")
    public User getAuthenticated() {
        return authService.getCurrentUser();
    }

    @PostMapping("/refresh/token")
    public AuthenticationResponse refreshToken(@Valid @RequestBody RefreshTokenRequest refquest) {
        return authService.refreshToken(refquest);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenService.deleterefreshToken(request.getRefreshToken());
        return ResponseEntity.status(OK).body(
                "Refresh Token deleted successfully !!"
        );
    }
}
