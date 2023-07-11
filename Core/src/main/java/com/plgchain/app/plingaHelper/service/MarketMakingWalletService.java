/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.dao.MarketMakingWalletDao;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMakingWallet;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

import jakarta.inject.Inject;

/**
 *
 */
public class MarketMakingWalletService extends BaseService<MarketMakingWallet> implements Serializable {

	private static final long serialVersionUID = -1912998788562981610L;

	@Inject
	private MarketMakingWalletDao marketMakingWalletDao;

	public Optional<MarketMakingWallet> findById(Long id) {
		return marketMakingWalletDao.findById(id);
	}

	public MarketMakingWallet save(MarketMakingWallet mmw) {
		return marketMakingWalletDao.save(mmw);
	}

	public MarketMakingWallet saveAndFlush(MarketMakingWallet mmw) {
		return marketMakingWalletDao.saveAndFlush(mmw);
	}

	public List<MarketMakingWallet> findAll() {
		return marketMakingWalletDao.findAll();
	}

	public boolean anyExist() {
		return marketMakingWalletDao.anyExist();
	}

	public void delete(MarketMakingWallet object) {
		marketMakingWalletDao.delete(object);
	}

	public void deleteAll() {
		marketMakingWalletDao.deleteAll();
	}

	public List<MarketMakingWallet> saveAll(List<MarketMakingWallet> oList) {
		return marketMakingWalletDao.saveAll(oList);
	}

	public List<MarketMakingWallet> findByContract(SmartContract contract) {
		return marketMakingWalletDao.findByContract(contract);
	}

	public long countByContract(SmartContract contract) {
		return marketMakingWalletDao.countByContract(contract);
	}

	public boolean existByContract(SmartContract contract) {
		return marketMakingWalletDao.existByContract(contract);
	}

	public List<MarketMakingWallet> findByContractAndWalletType(SmartContract contract, WalletType walletType) {
		return marketMakingWalletDao.findByContractAndWalletType(contract, walletType);
	}

	public long countByContractAndWalletType(SmartContract contract, WalletType walletType) {
		return marketMakingWalletDao.countByContractAndWalletType(contract, walletType);
	}

	public boolean existByContractAndWalletType(SmartContract contract, WalletType walletType) {
		return marketMakingWalletDao.existByContractAndWalletType(contract, walletType);
	}

}
