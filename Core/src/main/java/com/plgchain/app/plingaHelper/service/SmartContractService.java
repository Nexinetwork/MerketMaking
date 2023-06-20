/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dao.SmartContractDao;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

/**
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class SmartContractService extends BaseService implements Serializable {

	private static final long serialVersionUID = -8641271050704411082L;

	@Autowired
	private SmartContractDao smartContractDao;

	public SmartContract findById(Long id) {
		return smartContractDao.findById(id).get();
	}


	public SmartContract save(SmartContract smartContract) {
		return smartContractDao.save(smartContract);
	}

	public SmartContract saveAndFlush(SmartContract smartContract) {
		return smartContractDao.saveAndFlush(smartContract);
	}

	public List<SmartContract> findAll() {
		return smartContractDao.findAll();
	}

	public List<SmartContract> findByContractsAddress(String contractsAddress) {
		return smartContractDao.findByContractsAddress(contractsAddress);
	}

	public boolean existsSmartContractByContractsAddress(String contractsAddress) {
		return smartContractDao.existsSmartContractByContractsAddress(contractsAddress);
	}

	public Optional<SmartContract> findByBlockchainAndContractsAddress(Blockchain blockchain,String contractsAddress) {
		return smartContractDao.findByBlockchainAndContractsAddress(blockchain,contractsAddress);
	}

	public boolean existsSmartContractByBlockchainAndContractsAddress(Blockchain blockchain,String contractsAddress) {
		return smartContractDao.existsSmartContractByBlockchainAndContractsAddress(blockchain,contractsAddress);
	}

	public List<SmartContract> findByCoinAndContractsAddress(Coin coin,String contractsAddress) {
		return smartContractDao.findByCoinAndContractsAddress(coin,contractsAddress);
	}

	public boolean existsSmartContractByCoinAndContractsAddress(Coin coin,String contractsAddress) {
		return smartContractDao.existsSmartContractByCoinAndContractsAddress(coin,contractsAddress);
	}

	public Optional<SmartContract> findByBlockchainAndCoinAndContractsAddress(Blockchain blockchain,Coin coin,String contractsAddress) {
		return smartContractDao.findByBlockchainAndCoinAndContractsAddress(blockchain,coin,contractsAddress);
	}

	public boolean existsSmartContractByBlockchainAndCoinAndContractsAddress(Blockchain blockchain,Coin coin,String contractsAddress) {
		return smartContractDao.existsSmartContractByBlockchainAndCoinAndContractsAddress(blockchain,coin,contractsAddress);
	}

	public Optional<SmartContract> findByBlockchainAndCoin(Blockchain blockchain,Coin coin) {
		return smartContractDao.findByBlockchainAndCoin(blockchain,coin);
	}

	public boolean existsSmartContractByBlockchainAndCoin(Blockchain blockchain,Coin coin) {
		return smartContractDao.existsSmartContractByBlockchainAndCoin(blockchain,coin);
	}

	public List<SmartContract> findByMustAdd(boolean mustAdd) {
		return smartContractDao.findByMustAdd(mustAdd);
	}

}
