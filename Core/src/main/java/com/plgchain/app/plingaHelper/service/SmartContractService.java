/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.microService.SmartContractMicroService;

import jakarta.inject.Inject;

/**
 *
 */
@Service
public class SmartContractService implements Serializable {

	private static final long serialVersionUID = -5952931454431408763L;

	@Inject
	private SmartContractMicroService smartContractMicroService;

	public Optional<SmartContract> findById(long contractId) {
		return smartContractMicroService.findById(contractId);
	}

	public Optional<SmartContract> findByBlockchainAndContractsAddress(Blockchain blockchain, String contractsAddress) {
		return smartContractMicroService.findByBlockchainAndContractsAddress(blockchain, contractsAddress);
	}

	public boolean existsSmartContractByBlockchainAndContractsAddress(Blockchain blockchain, String contractsAddress) {
		return smartContractMicroService.existsSmartContractByBlockchainAndContractsAddress(blockchain, contractsAddress);
	}

	public SmartContract changeContractAddress(SmartContract sm,String newAddress) {
		sm.setContractsAddress(newAddress);
		return smartContractMicroService.save(sm);
	}

}
