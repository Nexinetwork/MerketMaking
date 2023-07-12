package com.plgchain.app.plingaHelper.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMaking;

public interface MarketMakingDao extends BaseLongDao<MarketMaking> {

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM MarketMaking e")
    public boolean anyExist();

	public Optional<MarketMaking> findBySmartContract(SmartContract smartContract);
	public boolean existsBySmartContract(SmartContract smartContract);

	public List<MarketMaking> findByInitialWalletCreationDone(boolean initialWalletCreationDone);
	public boolean existsByInitialWalletCreationDone(boolean initialWalletCreationDone);
	public long countByInitialWalletCreationDone(boolean initialWalletCreationDone);

	public List<MarketMaking> findByInitialWalletFundingDone(boolean initialWalletFundingDone);
	public boolean existsByInitialWalletFundingDone(boolean initialWalletFundingDone);
	public long countByInitialWalletFundingDone(boolean initialWalletFundingDone);

	public List<MarketMaking> findByInitialWalletCreationDoneAndInitialWalletFundingDone(boolean initialWalletCreationDone,boolean initialWalletFundingDone);
	public List<MarketMaking> findByInitialWalletCreationDoneOrInitialWalletFundingDone(boolean initialWalletCreationDone,boolean initialWalletFundingDone);

}
