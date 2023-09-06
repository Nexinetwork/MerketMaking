/**
 *
 */
package com.plgchain.app.plingaHelper.microService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plgchain.app.plingaHelper.coingecko.request.MustAddContractReq;
import com.plgchain.app.plingaHelper.dao.SmartContractDao;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.microService.Base.BaseMicroService;

/**
 *
 */
@Service
public class SmartContractMicroService extends BaseMicroService<SmartContract> implements Serializable {

	private static final long serialVersionUID = -8641271050704411082L;

	@Autowired
	private SmartContractDao smartContractDao;

	public Optional<SmartContract> findById(Long id) {
		return smartContractDao.findById(id);
	}

	public boolean existById(Long id) {
		return smartContractDao.existsById(id);
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

	public Optional<SmartContract> findByBlockchainAndContractsAddress(Blockchain blockchain, String contractsAddress) {
		return smartContractDao.findByBlockchainAndContractsAddress(blockchain, contractsAddress);
	}

	public boolean existsSmartContractByBlockchainAndContractsAddress(Blockchain blockchain, String contractsAddress) {
		return smartContractDao.existsSmartContractByBlockchainAndContractsAddress(blockchain, contractsAddress);
	}

	public List<SmartContract> findByCoinAndContractsAddress(Coin coin, String contractsAddress) {
		return smartContractDao.findByCoinAndContractsAddress(coin, contractsAddress);
	}

	public boolean existsSmartContractByCoinAndContractsAddress(Coin coin, String contractsAddress) {
		return smartContractDao.existsSmartContractByCoinAndContractsAddress(coin, contractsAddress);
	}

	public Optional<SmartContract> findByBlockchainAndCoinAndContractsAddress(Blockchain blockchain, Coin coin,
			String contractsAddress) {
		return smartContractDao.findByBlockchainAndCoinAndContractsAddress(blockchain, coin, contractsAddress);
	}

	public boolean existsSmartContractByBlockchainAndCoinAndContractsAddress(Blockchain blockchain, Coin coin,
			String contractsAddress) {
		return smartContractDao.existsSmartContractByBlockchainAndCoinAndContractsAddress(blockchain, coin,
				contractsAddress);
	}

	public Optional<SmartContract> findByBlockchainAndCoin(Blockchain blockchain, Coin coin) {
		return smartContractDao.findByBlockchainAndCoin(blockchain, coin);
	}

	public boolean existsSmartContractByBlockchainAndCoin(Blockchain blockchain, Coin coin) {
		return smartContractDao.existsSmartContractByBlockchainAndCoin(blockchain, coin);
	}

	public List<SmartContract> findByMustAdd(boolean mustAdd) {
		return smartContractDao.findByMustAdd(mustAdd);
	}

	@Transactional
	public List<MustAddContractReq> findByMustAddAsMustAddContractReq() {
		List<MustAddContractReq> lst = new ArrayList<MustAddContractReq>();
		smartContractDao.findByMustAdd(true).stream().forEach(contract -> {
			var mac = MustAddContractReq.builder().coin(contract.getCoin().getCoingeckoId())
					.blockchain(contract.getBlockchain().getCoingeckoId())
					.contractAddress(contract.getContractsAddress()).decimal(contract.getDecimal()).build();
			lst.add(mac);
		});
		return lst;
	}

	public boolean anyExist() {
		return smartContractDao.anyExist();
	}

	public void delete(SmartContract object) {
		smartContractDao.delete(object);
	}

	public void deleteAll() {
		smartContractDao.deleteAll();
	}

	public List<SmartContract> saveAll(List<SmartContract> oList) {
		return smartContractDao.saveAll(oList);
	}

}
