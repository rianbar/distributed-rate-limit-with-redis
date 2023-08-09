package com.example.limitation.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public static final String ADMIN_AUTHORITY = "ADMIN";
    public static  final String USER_AUTHORITY = "USER";
    public static final String USER_PASSWORD = "12345";

    @Autowired
    RequestFilter requestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(authorization -> authorization
                        .requestMatchers("/v1/route1").hasRole("ADMIN")
                        .requestMatchers("/v1/route2").hasRole("USER").anyRequest().authenticated())
                .addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService inMemoryUser() {
        UserDetails user = User.builder()
                .username("admin")
                .password(passwordEncoder().encode(USER_PASSWORD))
                .roles(ADMIN_AUTHORITY)
                .build();

        UserDetails user_two = User.builder()
                .username("user")
                .password(passwordEncoder().encode(USER_PASSWORD))
                .roles(USER_AUTHORITY)
                .build();

        return new InMemoryUserDetailsManager(user, user_two);
    }

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        return new InMemoryUserDetailsManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
