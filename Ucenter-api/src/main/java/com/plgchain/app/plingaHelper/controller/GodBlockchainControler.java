package com.plgchain.app.plingaHelper.controller;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plgchain.app.plingaHelper.util.MessageResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/godaction")
@RequiredArgsConstructor
public class GodBlockchainControler extends BaseController implements Serializable {

	private static final long serialVersionUID = -4038029722467775718L;

	private final static Logger logger = LoggerFactory.getLogger(GodBlockchainControler.class);

	@RequestMapping("/ping")
	public MessageResult ping() {
		try {
			return success("Pong God");
		} catch (Exception e) {
			logger.info("Error is : " + e.getMessage());
			return error(e.getMessage());
		}
	}

}
