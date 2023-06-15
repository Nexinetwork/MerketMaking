package com.plgchain.app.plingaHelper.security.service.impl;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.controller.AuthenticationController;
import com.plgchain.app.plingaHelper.entity.User;
import com.plgchain.app.plingaHelper.security.dao.request.SignUpRequest;
import com.plgchain.app.plingaHelper.security.dao.request.SigninRequest;
import com.plgchain.app.plingaHelper.security.dao.response.JwtAuthenticationResponse;
import com.plgchain.app.plingaHelper.security.service.AuthenticationService;
import com.plgchain.app.plingaHelper.security.service.JwtService;
import com.plgchain.app.plingaHelper.service.UserService;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final static Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
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
    	logger.info("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        logger.info("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
        var user = userService.findByEmailAddress(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        logger.info("cccccccccccccccccccccccccccccccccccccccccccccccccc");
        var jwt = jwtService.generateToken(user);
        logger.info("dddddddddddddddddddddddddddddddddddddddddddddddddddd");
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }
}