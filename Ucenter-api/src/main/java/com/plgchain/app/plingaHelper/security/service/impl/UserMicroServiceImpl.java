package com.plgchain.app.plingaHelper.security.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.microService.UserMicroService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserMicroServiceImpl implements com.plgchain.app.plingaHelper.security.service.UserMicroService {
    private final UserMicroService userMicroService;
    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return userMicroService.findByEmailAddress(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }
}