/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dao.MarketMakingDao;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMaking;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

import jakarta.inject.Inject;

/**
 *
 */
@Service
public class MarketMakingService extends BaseService<MarketMaking> implements Serializable {

	private static final long serialVersionUID = -3217338693572386600L;

	@Inject
	private MarketMakingDao marketMakingDao;

	public Optional<MarketMaking> findById(Long id) {
		return marketMakingDao.findById(id);
	}

	public boolean existById(Long id) {
		return marketMakingDao.existsById(id);
	}

	public MarketMaking save(MarketMaking mm) {
		return marketMakingDao.save(mm);
	}

	public MarketMaking saveAndFlush(MarketMaking mm) {
		return marketMakingDao.saveAndFlush(mm);
	}

	public List<MarketMaking> findAll() {
		return marketMakingDao.findAll();
	}

	public boolean anyExist() {
		return marketMakingDao.anyExist();
	}

	public void delete(MarketMaking object) {
		marketMakingDao.delete(object);
	}

	public void deleteAll() {
		marketMakingDao.deleteAll();
	}

	public List<MarketMaking> saveAll(List<MarketMaking> oList) {
		return marketMakingDao.saveAll(oList);
	}

	public Optional<MarketMaking> findBySmartContract(SmartContract smartContract) {
		return marketMakingDao.findBySmartContract(smartContract);
	}

	public boolean existsBySmartContract(SmartContract smartContract) {
		return marketMakingDao.existsBySmartContract(smartContract);
	}

	public List<MarketMaking> findByInitialWalletCreationDone(boolean initialWalletCreationDone) {
		return marketMakingDao.findByInitialWalletCreationDone(initialWalletCreationDone);
	}

	public boolean existsByInitialWalletCreationDone(boolean initialWalletCreationDone) {
		return marketMakingDao.existsByInitialWalletCreationDone(initialWalletCreationDone);
	}

	public long countByInitialWalletCreationDone(boolean initialWalletCreationDone) {
		return marketMakingDao.countByInitialWalletCreationDone(initialWalletCreationDone);
	}

	public List<MarketMaking> findByInitialWalletFundingDone(boolean initialWalletFundingDone) {
		return marketMakingDao.findByInitialWalletFundingDone(initialWalletFundingDone);
	}

	public boolean existsByInitialWalletFundingDone(boolean initialWalletFundingDone) {
		return marketMakingDao.existsByInitialWalletFundingDone(initialWalletFundingDone);
	}

	public long countByInitialWalletFundingDone(boolean initialWalletFundingDone) {
		return marketMakingDao.countByInitialWalletFundingDone(initialWalletFundingDone);
	}

	public List<MarketMaking> findByInitialWalletCreationDoneAndInitialWalletFundingDone(
			boolean initialWalletCreationDone, boolean initialWalletFundingDone) {
		return marketMakingDao.findByInitialWalletCreationDoneAndInitialWalletFundingDone(initialWalletCreationDone,
				initialWalletFundingDone);
	}

	public List<MarketMaking> findByInitialWalletCreationDoneOrInitialWalletFundingDone(
			boolean initialWalletCreationDone, boolean initialWalletFundingDone) {
		return marketMakingDao.findByInitialWalletCreationDoneOrInitialWalletFundingDone(initialWalletCreationDone,
				initialWalletFundingDone);
	}

	public Optional<MarketMaking> findTopByInitialWalletCreationDoneAndInitialWalletFundingDoneOrderByMarketMakingId(boolean initialWalletCreationDone,boolean initialWalletFundingDone){
		return marketMakingDao.findTopByInitialWalletCreationDoneAndInitialWalletFundingDoneOrderByMarketMakingId(initialWalletCreationDone,
				initialWalletFundingDone);
	}

	public List<MarketMaking> findByInitialWalletCreationDoneAndInitialWalletFundingDoneOrderByRandom(boolean initialWalletCreationDone,boolean initialWalletFundingDone) {
		return marketMakingDao.findByInitialWalletCreationDoneAndInitialWalletFundingDoneOrderByRandom(initialWalletCreationDone, initialWalletFundingDone);
	}

	public List<MarketMaking> findByInitialWalletCreationDoneOrInitialWalletFundingDoneOrderByRandom(boolean initialWalletCreationDone,boolean initialWalletFundingDone) {
		return marketMakingDao.findByInitialWalletCreationDoneOrInitialWalletFundingDoneOrderByRandom(initialWalletCreationDone, initialWalletFundingDone);
	}

}
