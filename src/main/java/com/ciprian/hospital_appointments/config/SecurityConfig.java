package com.ciprian.hospital_appointments.config;

import com.ciprian.hospital_appointments.config.exceptions.CustomAccesDeniedHandler;
import com.ciprian.hospital_appointments.config.exceptions.CustomAuthenticationEntryPointHandler;
import com.ciprian.hospital_appointments.config.security.AuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthFilter authFilter;
    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;
    private final CustomAccesDeniedHandler customAccesDeniedHandler;


    @Bean
    public SecurityFilterChain securityFilter(HttpSecurity http) {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .exceptionHandling(ex ->
                        ex.accessDeniedHandler(customAccesDeniedHandler)
                                .authenticationEntryPoint(customAuthenticationEntryPointHandler))
                .authorizeHttpRequests(req ->
                        req.requestMatchers("/api/auth/**", "/api/doctors/**", "/api/user/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html").permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
