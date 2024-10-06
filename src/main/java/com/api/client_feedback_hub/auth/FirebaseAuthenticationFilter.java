package com.api.client_feedback_hub.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        logger.info("Starting Firebase Authentication Filter");
        // Get the token from the Authorization header
        String idToken = request.getHeader("Authorization");

        // Check if the token is present and starts with "Bearer "
        if (idToken != null) {
            if (idToken.startsWith("Bearer ")) {
                idToken = idToken.substring(7);
            }
            logger.info("Received ID Token: " + idToken);
            try {
                // Verify the Firebase token
                FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

                String uid = firebaseToken.getUid();

                List<GrantedAuthority> authorities = getAuthoritiesFromToken(firebaseToken);

                SecurityContextHolder.getContext().setAuthentication(new FirebaseAuthenticationToken(idToken, firebaseToken, authorities));
//                // Set the authentication in the security context
                SecurityContextHolder.getContext().getAuthentication().setAuthenticated(true);
                logger.info("Authentication successful for UID: " + uid);
            } catch (Exception e) {
                // Handle token validation errors
                logger.error("Firebase Token is invalid: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Firebase Token");
                return; // Stop further processing
            }
        } else {
            logger.warn("No Authorization header found or it does not start with 'Bearer '");
        }
        // Continue the filter chain
        filterChain.doFilter(request, response);
        logger.info("Finished Firebase Authentication Filter");
    }

    private List<GrantedAuthority> getAuthoritiesFromToken(FirebaseToken token) {
        Object claims = token.getClaims().get("authorities");
        List<String> permissions = (List<String>) claims;
        List<GrantedAuthority> authorities = AuthorityUtils.NO_AUTHORITIES;
        if (permissions != null && !permissions.isEmpty()) {
            authorities = AuthorityUtils.createAuthorityList(permissions);
            logger.info("User roles: {}", String.join(", ", permissions));
        }
        return authorities;
    }
}