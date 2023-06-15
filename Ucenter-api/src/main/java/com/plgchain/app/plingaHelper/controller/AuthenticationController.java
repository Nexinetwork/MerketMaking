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
import com.plgchain.app.plingaHelper.util.MessageResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

/**
 *
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController extends BaseController implements Serializable {

	private static final long serialVersionUID = -1153034969946962877L;

	private final static Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

	private final AuthenticationService authenticationService;

	@PostMapping("/signup")
	public MessageResult signup(@RequestBody SignUpRequest request) {
		return success(authenticationService.signup(request));

	}

	@PostMapping("/signin")
	public MessageResult signin(@RequestBody SigninRequest request) {
		try {
			return success(authenticationService.signin(request));
		} catch (Exception e) {
			return error(e.getMessage());
		}
	}

	@RequestMapping("/ping")
	public ResponseEntity<String> ping() {
		return ResponseEntity.ok("Pong");
	}

}
