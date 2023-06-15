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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

/**
 *
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController implements Serializable {

	private static final long serialVersionUID = -1153034969946962877L;

	private final static Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

	private final AuthenticationService authenticationService;

	@PostMapping("/signup")
	public ResponseEntity<JwtAuthenticationResponse> signup(@RequestBody SignUpRequest request) {
		logger.info("aaaaaaaaaaaa" + request.toString());
		return ResponseEntity.ok(authenticationService.signup(request));
	}

	@PostMapping("/signin")
	public ResponseEntity<String> signin(@RequestBody SigninRequest request) {
		try {
			return ResponseEntity.ok(authenticationService.signin(request).getToken());
		} catch (Exception e) {
			logger.warn("Errrrrrror" + e.getMessage());
			if (e.getMessage().contains("Invalid email or password"))
				return ResponseEntity.ok("Invalid email or password");
		}
		return ResponseEntity.ok("");
	}

	@RequestMapping("/ping")
	public ResponseEntity<String> ping() {
		return ResponseEntity.ok("Pong");
	}

}
