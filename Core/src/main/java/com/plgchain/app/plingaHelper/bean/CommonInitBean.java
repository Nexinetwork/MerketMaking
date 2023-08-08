/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

import com.plgchain.app.plingaHelper.service.SystemConfigService;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 */
@Component
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommonInitBean implements Serializable {

	private static final long serialVersionUID = -3669045796530377728L;

	private List<String> restartingBlockchain = new ArrayList<String>();

	private String privateKey;

	@Inject
	private SystemConfigService systemConfigService;

	@PostConstruct
	public void init() {
		if (systemConfigService.isByConfigNameExist("ssh-key-path"))
			privateKey = systemConfigService.findByConfigName("ssh-key-path").getConfigStringValue();
	}

	public boolean doesBlockchainRestarting(String blockchain) {
		return restartingBlockchain.contains(blockchain);
	}

	public void startBlockchainRestarting(String blockchain) {
		restartingBlockchain.add(blockchain);
	}

	public void stopBlockchainRestarting(String blockchain) {
		restartingBlockchain.remove(blockchain);
	}

}
