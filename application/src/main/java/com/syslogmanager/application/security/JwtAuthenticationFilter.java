package com.syslogmanager.application.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    private com.syslogmanager.application.repository.SessionRepository sessionRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = null;
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // If session-based logout is used, ensure the session (sid) exists
                DecodedJWT decoded = jwtUtil.validateToken(token);
                String sid = null;
                try { sid = decoded.getClaim("sid").asString(); } catch (Exception ignore) {}
                if (sid != null) {
                    if (sessionRepository == null || !sessionRepository.existsBySessionId(sid)) {
                        // session removed (logged out) — reject with 401
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session invalidated");
                        return;
                    }
                } else {
                    // If no sid is present, disallow access for protected endpoints.
                    // Allow the logout endpoint to proceed so it can return a helpful message.
                    String path = request.getRequestURI();
                    if (path != null && path.equals("/api/auth/logout")) {
                        // allow controller to handle logout even if token lacks sid
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session invalidated");
                        return;
                    }
                }

                String username = decoded.getSubject();
                var userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception ex) {
                // invalid token, continue without auth
            }
        }

        filterChain.doFilter(request, response);
    }

}
