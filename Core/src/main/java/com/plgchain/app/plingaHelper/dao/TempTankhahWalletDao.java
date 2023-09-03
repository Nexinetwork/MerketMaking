package com.plgchain.app.plingaHelper.dao;

import java.util.List;

import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.entity.TempTankhahWallet;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;

public interface TempTankhahWalletDao extends BaseLongDao<TempTankhahWallet> {
	public List<TempTankhahWallet> findBySmartContractAndWalletType(SmartContract smartContract,WalletType walletType);

	public long countBySmartContractAndWalletType(SmartContract smartContract, WalletType walletType);

	public boolean existsBySmartContractAndWalletType(SmartContract smartContract, WalletType walletType);

}
