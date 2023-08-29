/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.plgchain.app.plingaHelper.dao.MMWalletDao;
import com.plgchain.app.plingaHelper.dto.MarketMakingWalletDto;
import com.plgchain.app.plingaHelper.entity.MMWallet;
import com.plgchain.app.plingaHelper.service.Base.BaseService;
import com.plgchain.app.plingaHelper.util.SecurityUtil;

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

	public List<MarketMakingWalletDto> addTransferWallet(MMWallet mmw, MarketMakingWalletDto mmwd, String secretKey) {
		List<MarketMakingWalletDto> mmwdLst = mmw.getTransferWalletList();
		mmwd = fixMMWallet(mmwd, secretKey);
		if (!mmwdLst.contains(mmwd)) {
			mmwdLst.add(mmwd);
			mmw.setTransferWalletList(mmwdLst);
		}
		return mmwdLst;
	}

	public List<MarketMakingWalletDto> addTransferWallet(MMWallet mmw, List<MarketMakingWalletDto> mmwdNewLst,
			String secretKey) {
		List<MarketMakingWalletDto> mmwdlLst = mmw.getTransferWalletList();
		mmwdlLst.addAll(mmwdNewLst.stream().map(mmwd -> fixMMWallet(mmwd, secretKey))
				.filter(fixedMmwd -> !mmwdlLst.contains(fixedMmwd)).collect(Collectors.toList()));
		mmw.setTransferWalletList(mmwdlLst);
		return mmwdlLst;
	}

	public List<MarketMakingWalletDto> addDefiWallet(MMWallet mmw, MarketMakingWalletDto mmwd, String secretKey) {
		List<MarketMakingWalletDto> mmwdLst = mmw.getDefiWalletList();
		mmwd = fixMMWallet(mmwd, secretKey);
		if (!mmwdLst.contains(mmwd)) {
			mmwdLst.add(mmwd);
			mmw.setDefiWalletList(mmwdLst);
		}
		return mmwdLst;
	}

	public List<MarketMakingWalletDto> addDefiWallet(MMWallet mmw, List<MarketMakingWalletDto> mmwdNewLst,
			String secretKey) {
		List<MarketMakingWalletDto> mmwdlLst = mmw.getDefiWalletList();
		mmwdlLst.addAll(mmwdNewLst.stream().map(mmwd -> fixMMWallet(mmwd, secretKey))
				.filter(fixedMmwd -> !mmwdlLst.contains(fixedMmwd)).collect(Collectors.toList()));
		mmw.setDefiWalletList(mmwdlLst);
		return mmwdlLst;
	}

	public MarketMakingWalletDto fixMMWallet(MarketMakingWalletDto mmwd, String secretKey) {
		mmwd.setEncryptedPrivateKey(SecurityUtil.encryptString(mmwd.getPrivateKeyHex(), secretKey));
		return mmwd;
	}

	public List<MMWallet> findAll() {
		return mmWalletDao.findAll();
	}

	public MMWallet save(MMWallet mmw) {
		return mmWalletDao.save(mmw);
	}

}
