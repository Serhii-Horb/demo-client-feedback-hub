//package com.api.client_feedback_hub.auth;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//@EnableMethodSecurity
//public class SecurityConfig {
//
//    /**
//     * The JWT filter bean for processing JWT tokens.
//     */
//    private final FirebaseAuthFilter firebaseAuthFilter;
//    public static final String USER_ROLE = "USER";
//    public static final String ADMIN_ROLE = "ADMIN";
//    /**
//     * Configures the security filter chain.
//     * <p>
//     * This method sets up the security configurations such as CSRF disabling, session management policy,
//     * authorization rules and adds the JWT filter after the UsernamePasswordAuthenticationFilter.
//     * </p>
//     *
//     * @param http the HttpSecurity instance to configure.
//     * @return the SecurityFilterChain instance.
//     * @throws Exception if an error occurs during configuration.
//     */
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http.csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
//                .authorizeHttpRequests(
//                        authz -> authz
//                                .requestMatchers(
//                                        ",/**"
//                                )
////                                .requestMatchers(
////                                        "/auth/register",
////                                        "/auth/login",
////                                        "/auth/token",
////                                        "/manage/**",
////                                        "/swagger-ui.html",
////                                        "/api/v1/auth/**",
////                                        "/v3/api-docs/**",
////                                        "/swagger-ui/**",
////                                        "/api/feedback/register"
////                                        /*",/**"*/
////                                )
//                                .permitAll()
////                                .anyRequest().authenticated()
//                                                  .anyRequest().permitAll()
//                ).addFilterAfter(firebaseAuthFilter, UsernamePasswordAuthenticationFilter.class).build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}