/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import com.plgchain.app.plingaHelper.dao.MMWalletDao;
import com.plgchain.app.plingaHelper.entity.MMWallet;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

import jakarta.inject.Inject;

/**
 *
 */
public class MMWalletService extends BaseService<MMWallet> implements Serializable {

	private static final long serialVersionUID = 1834752888558756630L;

	@Inject
	private MMWalletDao mmWalletDao;

	public boolean isEmptyDocument() {
	    return mmWalletDao.count() == 0;
	}

	public Optional<MMWallet> findById(long marketMakingId) {
		return mmWalletDao.findById(marketMakingId);
	}

	public List<MMWallet> findAll() {
		return mmWalletDao.findAll();
	}

	public MMWallet save(MMWallet mmw) {
		return mmWalletDao.save(mmw);
	}

}
