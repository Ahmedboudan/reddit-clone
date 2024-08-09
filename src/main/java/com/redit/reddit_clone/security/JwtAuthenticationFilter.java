package com.redit.reddit_clone.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redit.reddit_clone.dto.ErrorResponse;
import com.redit.reddit_clone.exceptions.SpringRedditException;
import com.redit.reddit_clone.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import io.jsonwebtoken.ExpiredJwtException;

@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtFromRequest = getJwtFromRequest(request);

        try {
            if (StringUtils.hasText(jwtFromRequest) && jwtProvider.validateToken(jwtFromRequest)) {
                log.info("Token valid");
                String username = jwtProvider.getUsernameFromJwt(jwtFromRequest);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                setAuthenticationContext(userDetails, request);
            }
        } catch (Exception e) {
            log.warn("JWT expired: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            ErrorResponse errorResponse = new ErrorResponse("Unauthorized", e.getMessage());

            response.getWriter().write(
                    new ObjectMapper().writeValueAsString(errorResponse)
            );
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthenticationContext(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            log.info("Getting JWT from request");
            return bearerToken.substring(7);
        }
        return null;
    }
}
