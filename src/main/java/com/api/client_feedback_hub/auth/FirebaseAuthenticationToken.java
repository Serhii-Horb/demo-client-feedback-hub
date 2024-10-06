//package com.api.client_feedback_hub.auth;
//
//import org.springframework.security.authentication.AbstractAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Collection;
//
//public class FirebaseAuthenticationToken implements Authentication {
//
//    private final UserDetails user; // Информация о пользователе
//    private final String token; // Токен Firebase
//    private final Collection<? extends GrantedAuthority> authorities; // Роли пользователя
//    private boolean authenticated; // Статус аутентификации
//
//    // Конструктор
//    public FirebaseAuthenticationToken(UserDetails user, String token, Collection<? extends GrantedAuthority> authorities) {
//        this.user = user;
//        this.token = token;
//        this.authorities = authorities;
//        this.authenticated = true; // По умолчанию аутентифицирован
//    }
//
//    @Override
//    public String getName() {
//        return user.getUsername(); // Возвращает имя пользователя (например, UID)
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return authorities; // Возвращает роли пользователя
//    }
//
//    @Override
//    public Object getCredentials() {
//        return token; // Возвращает токен
//    }
//
//    @Override
//    public Object getDetails() {
//        return null; // Можно вернуть дополнительные детали, если нужно
//    }
//
//    @Override
//    public Object getPrincipal() {
//        return user; // Возвращает информацию о пользователе
//    }
//
//    @Override
//    public boolean isAuthenticated() {
//        return authenticated; // Возвращает статус аутентификации
//    }
//
//    @Override
//    public void setAuthenticated(boolean isAuthenticated) {
//        this.authenticated = isAuthenticated; // Устанавливает статус аутентификации
//    }
//}