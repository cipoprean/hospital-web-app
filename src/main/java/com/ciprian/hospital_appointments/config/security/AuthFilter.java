package com.ciprian.hospital_appointments.config.security;

import com.ciprian.hospital_appointments.config.exceptions.CustomAuthenticationEntryPointHandler;
import com.ciprian.hospital_appointments.service.CustomUserDetailsService;
import com.ciprian.hospital_appointments.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //1 luam token-ul din request
        String token = extactTokenFromRequest(request);
        //2 daca nu e null, luam username-ul din email
        if (Objects.nonNull(token)) {
            String email = "";
            try {
                email = jwtService.getUsernameFromToken(token);
            } catch (Exception ex) {
                AuthenticationException authenticationException = new BadCredentialsException(ex.getMessage());
                customAuthenticationEntryPointHandler.commence(request, response, authenticationException);
                return;
            }
            //3 executam load by username
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            if (StringUtils.hasText(email) && jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private String extactTokenFromRequest(HttpServletRequest request) {
        String tokenWithBearer = request.getHeader("Authorization");
        if (tokenWithBearer != null && tokenWithBearer.startsWith("Bearer ")) {
            return tokenWithBearer.substring(7);
        }

        return null;
    }
}
