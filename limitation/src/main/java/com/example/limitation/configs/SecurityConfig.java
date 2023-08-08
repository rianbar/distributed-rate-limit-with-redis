package com.example.limitation.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public static final String ADMIN_AUTHORITY = "ADMIN";
    public static  final String USER_AUTHORITY = "USER";
    public static final String USER_PASSWORD = "12345";
    public static final String API_ROUTE = "/v1";

    @Autowired
    RequestFilter requestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(authorization -> authorization
                        .requestMatchers(HttpMethod.GET, API_ROUTE + "/**")
                        .hasRole("ADMIN").anyRequest().authenticated());

        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService inMemoryUser() {
        UserDetails user = User.builder()
                .username("admin")
                .password(passwordEncoder().encode(USER_PASSWORD))
                .authorities(ADMIN_AUTHORITY)
                .build();

        UserDetails user_two = User.builder()
                .username("user")
                .password(passwordEncoder().encode(USER_PASSWORD))
                .authorities(USER_AUTHORITY)
                .build();

        return new InMemoryUserDetailsManager(user, user_two);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
