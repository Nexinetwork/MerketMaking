/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dao.TankhahWalletDao;
import com.plgchain.app.plingaHelper.entity.TankhahWallet;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

import jakarta.inject.Inject;

/**
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class TankhahWalletService extends BaseService implements Serializable {

	private static final long serialVersionUID = -889011709682489433L;

	@Inject
	private TankhahWalletDao tankhahWalletDao;

	public boolean existsTankhahWalletByContract(SmartContract contract) {
		return tankhahWalletDao.existsTankhahWalletByContract(contract);
	}

	public List<TankhahWallet> findByContract(SmartContract contract) {
		return tankhahWalletDao.findByContract(contract);
	}

	public Optional<TankhahWallet> findById(Long id) {
		return tankhahWalletDao.findById(id);
	}

	public TankhahWallet save(TankhahWallet tw) {
		return tankhahWalletDao.save(tw);
	}

	public TankhahWallet saveAndFlush(TankhahWallet tw) {
		return tankhahWalletDao.saveAndFlush(tw);
	}

	public List<TankhahWallet> findAll() {
		return tankhahWalletDao.findAll();
	}

}