/**
 *
 */
package com.plgchain.app.plingaHelper.controller;

import java.io.Serializable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plgchain.app.plingaHelper.security.dao.request.SignUpRequest;
import com.plgchain.app.plingaHelper.security.dao.request.SigninRequest;
import com.plgchain.app.plingaHelper.security.dao.response.JwtAuthenticationResponse;
import com.plgchain.app.plingaHelper.security.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

/**
 *
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController implements Serializable {

	private static final long serialVersionUID = -1153034969946962877L;


	private final AuthenticationService authenticationService;
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> signup(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authenticationService.signup(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SigninRequest request) {
        return ResponseEntity.ok(authenticationService.signin(request));
    }



}
