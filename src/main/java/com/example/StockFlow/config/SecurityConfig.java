//// java
//package com.example.StockFlow.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.HttpStatusEntryPoint;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password(passwordEncoder.encode("adminPass"))
//                .roles("ADMIN")
//                .build();
//
//        UserDetails wm = User.builder()
//                .username("wm")
//                .password(passwordEncoder.encode("wmPass"))
//                .roles("WAREHOUSE_MANAGER")
//                .build();
//
//        UserDetails client = User.builder()
//                .username("client")
//                .password(passwordEncoder.encode("clientPass"))
//                .roles("CLIENT")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, wm, client);
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .cors(cors -> cors.disable())
//                .csrf(csrf -> csrf.disable())
//
//                .httpBasic(Customizer.withDefaults())
//
//                .logout(logout -> logout
//                        .logoutUrl("/api/auth/logout")
//                        .logoutSuccessHandler((request, response, authentication) -> {
//                            response.setHeader("WWW-Authenticate", "Basic realm=\"SmartSupply\"");
//                            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Logged out");
//                        })
//                        .permitAll()
//                )
//
//                .authorizeHttpRequests(auth -> auth
//                        // endpoints publics
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
//                        // administration
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/api/products/category/**").hasRole("ADMIN")
//                        // warehouse
//                        .requestMatchers("/api/inventory/**").hasAnyRole("WAREHOUSE_MANAGER", "ADMIN")
//                        .requestMatchers("/api/shipments/**").hasAnyRole("WAREHOUSE_MANAGER", "ADMIN")
//                        // commandes clients
//                        .requestMatchers("/api/orders/**").hasAnyRole("CLIENT", "ADMIN")
//                        // tout le reste de /api requiert authentification
//                        .requestMatchers("/api/**").authenticated()
//                )
//
//                .exceptionHandling(ex -> ex
//                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
//                        .accessDeniedHandler((request, response, accessDeniedException) ->
//                                response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied"))
//                );
//
//        return http.build();
//    }
//
//}
