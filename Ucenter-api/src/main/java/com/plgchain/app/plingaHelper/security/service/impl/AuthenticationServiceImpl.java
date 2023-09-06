package com.plgchain.app.plingaHelper.security.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.entity.User;
import com.plgchain.app.plingaHelper.security.dao.request.SignUpRequest;
import com.plgchain.app.plingaHelper.security.dao.request.SigninRequest;
import com.plgchain.app.plingaHelper.security.dao.response.JwtAuthenticationResponse;
import com.plgchain.app.plingaHelper.security.service.AuthenticationService;
import com.plgchain.app.plingaHelper.security.service.JwtService;
import com.plgchain.app.plingaHelper.microService.UserService;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Override
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        var user = User.builder().firstname(request.getFirstName()).lastname(request.getLastName())
                .emailAddress(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
                //.role(Role.USER)
                .build();
        userService.save(user);
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    @Override
    public JwtAuthenticationResponse signin(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userService.findByEmailAddress(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }
}