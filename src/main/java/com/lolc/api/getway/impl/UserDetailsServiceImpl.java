package com.lolc.api.getway.impl;

import com.lolc.api.getway.entity.User;
import com.lolc.api.getway.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        String normalizedPhone = phoneNumber == null ? null : phoneNumber.trim();
        User user = authRepository.findByPhoneNumber(normalizedPhone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for phone number: " + normalizedPhone));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getPhoneNumber())
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}
