/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	private Set<String> restartingBlockchain = new HashSet<String>();

	private Set<String> restartingNode = new HashSet<String>();

	private Set<String> shouldRecalcNonce = new HashSet<String>();

	private String privateKey;

	@Inject
	private SystemConfigService systemConfigService;

	@PostConstruct
	public void init() {
		if (systemConfigService.isByConfigNameExist("ssh-key-path"))
			privateKey = systemConfigService.findByConfigName("ssh-key-path").get().getConfigStringValue();
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

	public boolean doesNodeRestarting(String rpcUrl) {
		return restartingNode.contains(rpcUrl);
	}

	public void startNodeRestarting(String rpcUrl) {
		restartingNode.add(rpcUrl);
	}

	public void stopNodeRestarting(String rpcUrl) {
		restartingNode.remove(rpcUrl);
	}

	public boolean doesShouldRecalcNonce(String address) {
		return shouldRecalcNonce.contains(address);
	}

	public void startShouldRecalcNonce(String address) {
		shouldRecalcNonce.add(address);
	}

	public void stopShouldRecalcNonce(String address) {
		shouldRecalcNonce.remove(address);
	}

}
