package com.book.store.app.config;

import com.book.store.app.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig
    ) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/registration",
                                "/api/auth/login")
                        .permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/**", "/api/categories/**",
                                "/api/cart/**", "/api/orders/**")
                        .hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/cart",
                                "/api/orders")
                        .hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/cart/cart-items/**")
                        .hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/cart/cart-items/**")
                        .hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH, "/api/orders/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/books/**",
                                "/api/categories/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/books/**",
                                "/api/categories/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**",
                                "/api/categories/**")
                        .hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter
                        .class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
