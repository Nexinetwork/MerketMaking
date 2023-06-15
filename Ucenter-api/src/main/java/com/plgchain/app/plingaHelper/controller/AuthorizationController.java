/**
 *
 */
package com.plgchain.app.plingaHelper.controller;

import java.io.Serializable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 *
 */
@RestController
@RequestMapping("/api/v1/resource")
@RequiredArgsConstructor
public class AuthorizationController implements Serializable {

	private static final long serialVersionUID = -1512739281705970680L;

	@GetMapping
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Here is your resource");
    }

}
