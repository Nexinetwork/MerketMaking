package com.plgchain.app.plingaHelper.security.service;

import com.plgchain.app.plingaHelper.security.dao.request.SignUpRequest;
import com.plgchain.app.plingaHelper.security.dao.request.SigninRequest;
import com.plgchain.app.plingaHelper.security.dao.response.JwtAuthenticationResponse;

public interface AuthenticationService {
	public JwtAuthenticationResponse signup(SignUpRequest request);

	public JwtAuthenticationResponse signin(SigninRequest request);
}
