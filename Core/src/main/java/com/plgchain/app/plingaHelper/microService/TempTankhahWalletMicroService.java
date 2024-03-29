package com.plgchain.app.plingaHelper.microService;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.annotation.LogMethod;
import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.dao.TempTankhahWalletDao;
import com.plgchain.app.plingaHelper.entity.TempTankhahWallet;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.microService.Base.BaseMicroService;

import jakarta.inject.Inject;

@Service
public class TempTankhahWalletMicroService extends BaseMicroService<TempTankhahWallet> implements Serializable {

	private static final long serialVersionUID = -984115045697781591L;

	@Inject
	private TempTankhahWalletDao tempTankhahWalletDao;

	public long countBySmartContractAndWalletType(SmartContract smartContract, WalletType walletType) {
		return tempTankhahWalletDao.countBySmartContractAndWalletType(smartContract, walletType);
	}

	public List<TempTankhahWallet> findBySmartContractAndWalletType(SmartContract smartContract,WalletType walletType) {
		return tempTankhahWalletDao.findBySmartContractAndWalletType(smartContract, walletType);
	}

	public Optional<TempTankhahWallet> findById(Long id) {
		return tempTankhahWalletDao.findById(id);
	}

	public boolean existById (Long id) {
		return tempTankhahWalletDao.existsById(id);
	}

	@LogMethod
	public TempTankhahWallet save(TempTankhahWallet ttw) {
		return tempTankhahWalletDao.save(ttw);
	}

	public TempTankhahWallet saveAndFlush(TempTankhahWallet ttw) {
		return tempTankhahWalletDao.saveAndFlush(ttw);
	}

    public void delete(TempTankhahWallet ttw) {
    	tempTankhahWalletDao.delete(ttw);
	}

	public void deleteAll() {
		tempTankhahWalletDao.deleteAll();
	}

	public List<TempTankhahWallet> saveAll(List<TempTankhahWallet> oList) {
		return tempTankhahWalletDao.saveAll(oList);
	}

	public List<TempTankhahWallet> saveAllAndFlush(List<TempTankhahWallet> oList) {
		return tempTankhahWalletDao.saveAllAndFlush(oList);
	}

	public boolean existsBySmartContractAndWalletType(SmartContract smartContract, WalletType walletType) {
		return tempTankhahWalletDao.existsBySmartContractAndWalletType(smartContract, walletType);
	}

}
