package com.api.client_feedback_hub.auth;

import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FirebaseAuthFilter extends OncePerRequestFilter {

    private final FirebaseAuthService firebaseAuthService;

    public FirebaseAuthFilter(FirebaseAuthService firebaseAuthService) {
        this.firebaseAuthService = firebaseAuthService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");

        // Получаем токен из заголовка запроса
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String idToken = authHeader.substring(7); // удаляем "Bearer " из токена
            try {
                FirebaseToken decodedToken = firebaseAuthService.verifyToken(idToken);
                String uid = decodedToken.getUid(); // Получаем UID пользователя

                // Извлекаем роли из токена (например, если роли передаются в claims)
                List<GrantedAuthority> authorities = new ArrayList<>();
                // Проверяем наличие роли и добавляем в список
                if (decodedToken.getClaims().containsKey("role")) {
                    String role = (String) decodedToken.getClaims().get("role");
                    authorities.add(new SimpleGrantedAuthority(role));
                }

                // Создаем объект User с ролями
                User user = new User(uid, "", authorities);
                // Создаем объект FirebaseAuthenticationToken
                FirebaseAuthenticationToken authenticationToken = new FirebaseAuthenticationToken(user, idToken, authorities);
                // Устанавливаем аутентификацию в SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
            filterChain.doFilter(request, response);
    }
}