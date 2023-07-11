package com.plgchain.app.plingaHelper.dao;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMakingWallet;

public interface MarketMakingWalletDao extends BaseLongDao<MarketMakingWallet> {

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM MarketMakingWallet e")
	public boolean anyExist();
	public List<MarketMakingWallet> findByContract(SmartContract contract);
	public long countByContract(SmartContract contract);
	public boolean existsByContract(SmartContract contract);
	public List<MarketMakingWallet> findByContractAndWalletType(SmartContract contract, WalletType walletType);
	public long countByContractAndWalletType(SmartContract contract, WalletType walletType);
	public boolean existsByContractAndWalletType(SmartContract contract, WalletType walletType);
}
