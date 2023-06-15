package com.plgchain.app.plingaHelper.controller;

import java.io.Serializable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/godaction")
@RequiredArgsConstructor
public class GodBlockchainControler extends BaseController implements Serializable {

	private static final long serialVersionUID = -4038029722467775718L;

	@RequestMapping("/ping")
	public ResponseEntity<String> ping() {
		return ResponseEntity.ok("Pong");
	}

}
