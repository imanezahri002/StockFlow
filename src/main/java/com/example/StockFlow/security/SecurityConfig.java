package com.example.StockFlow.security;

import com.example.StockFlow.entity.enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static java.lang.String.valueOf;

@Configuration
public class SecurityConfig {

    // 1️⃣ Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2️⃣ Utilisateurs InMemory
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {

        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("admin123"))
                .roles(valueOf(Role.ADMIN))
                .build();

        UserDetails manager = User.builder()
                .username("manager")
                .password(encoder.encode("manager123"))
                .roles(valueOf(Role.WAREHOUSE_MANAGER))
                .build();

        UserDetails client = User.builder()
                .username("client")
                .password(encoder.encode("client123"))
                .roles(valueOf(Role.CLIENT))
                .build();

        return new InMemoryUserDetailsManager(admin, manager, client);
    }

    // 3️⃣ Security Filter Chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // API REST → pas de CSRF
                .csrf(csrf -> csrf.disable())

                // Stateless (pas de session)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Autorisations
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole(valueOf(Role.ADMIN))
                        .requestMatchers("/api/products/**").hasRole(valueOf(Role.ADMIN))
                        .requestMatchers("/api/inventory/**").hasAnyRole(valueOf(Role.ADMIN), valueOf(Role.WAREHOUSE_MANAGER))
                        .requestMatchers("/api/shipments/**").hasAnyRole(valueOf(Role.ADMIN), valueOf(Role.WAREHOUSE_MANAGER))
                        .requestMatchers("/api/orders/**").hasAnyRole(valueOf(Role.CLIENT), valueOf(Role.ADMIN))
                        .anyRequest().authenticated()
                )

                // Basic Auth
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
