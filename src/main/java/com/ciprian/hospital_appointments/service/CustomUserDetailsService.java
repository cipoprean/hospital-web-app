package com.ciprian.hospital_appointments.service;

import com.ciprian.hospital_appointments.config.exceptions.NotFoundException;
import com.ciprian.hospital_appointments.config.security.CustomUserDetails;
import com.ciprian.hospital_appointments.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("Utilizatorul nu exista"));

        return CustomUserDetails.builder()
                .user(user)
                .build();
    }
}
