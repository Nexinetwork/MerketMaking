/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dao.MMWalletDao;
import com.plgchain.app.plingaHelper.dto.MarketMakingWalletDto;
import com.plgchain.app.plingaHelper.entity.MMWallet;
import com.plgchain.app.plingaHelper.service.Base.BaseService;
import com.plgchain.app.plingaHelper.util.SecurityUtil;

import jakarta.inject.Inject;

/**
 *
 */
@Service
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

	public List<MarketMakingWalletDto> addTransferWallet(MMWallet mmw, MarketMakingWalletDto mmwd, String secretKey) throws IOException {
		List<MarketMakingWalletDto> mmwdLst = mmw.getTransferWalletList();
		mmwd = fixMMWallet(mmwd, secretKey);
		if (!mmwdLst.contains(mmwd)) {
			mmwdLst.add(mmwd);
			mmw.setTransferWalletList(mmwdLst);
			save(mmw);
		}
		return mmwdLst;
	}

	public List<MarketMakingWalletDto> addTransferWallet(MMWallet mmw, List<MarketMakingWalletDto> mmwdNewLst,
			String secretKey) throws IOException {
		List<MarketMakingWalletDto> mmwdlLst = mmw.getTransferWalletList();
		mmwdlLst.addAll(mmwdNewLst.stream().map(mmwd -> fixMMWallet(mmwd, secretKey))
				.filter(fixedMmwd -> !mmwdlLst.contains(fixedMmwd)).collect(Collectors.toList()));
		mmw.setTransferWalletList(mmwdlLst);
		save(mmw);
		return mmwdlLst;
	}

	public List<MarketMakingWalletDto> setTransferWallet(MMWallet mmw, List<MarketMakingWalletDto> mmwdNewLst,
			String secretKey) throws IOException {
		List<MarketMakingWalletDto> mmwdlLst = new ArrayList<MarketMakingWalletDto>();
		mmwdlLst.addAll(mmwdNewLst.stream().map(mmwd -> fixMMWallet(mmwd, secretKey)).collect(Collectors.toList()));
		mmw.setTransferWalletList(mmwdlLst);
		save(mmw);
		return mmwdlLst;
	}

	public List<MarketMakingWalletDto> setDefiWallet(MMWallet mmw, List<MarketMakingWalletDto> mmwdNewLst,
			String secretKey) throws IOException {
		List<MarketMakingWalletDto> mmwdlLst = new ArrayList<MarketMakingWalletDto>();
		mmwdlLst.addAll(mmwdNewLst.stream().map(mmwd -> fixMMWallet(mmwd, secretKey)).collect(Collectors.toList()));
		mmw.setDefiWalletList(mmwdlLst);
		save(mmw);
		return mmwdlLst;
	}

	public List<MarketMakingWalletDto> addDefiWallet(MMWallet mmw, MarketMakingWalletDto mmwd, String secretKey) throws IOException {
		List<MarketMakingWalletDto> mmwdLst = mmw.getDefiWalletList();
		mmwd = fixMMWallet(mmwd, secretKey);
		if (!mmwdLst.contains(mmwd)) {
			mmwdLst.add(mmwd);
			mmw.setDefiWalletList(mmwdLst);
			save(mmw);
		}
		return mmwdLst;
	}

	public List<MarketMakingWalletDto> addDefiWallet(MMWallet mmw, List<MarketMakingWalletDto> mmwdNewLst,
			String secretKey) {
		List<MarketMakingWalletDto> mmwdlLst = mmw.getDefiWalletList();
		mmwdlLst.addAll(mmwdNewLst.stream().map(mmwd -> fixMMWallet(mmwd, secretKey))
				.filter(fixedMmwd -> !mmwdlLst.contains(fixedMmwd)).collect(Collectors.toList()));
		mmw.setDefiWalletList(mmwdlLst);
		save(mmw);
		return mmwdlLst;
	}

	public List<MarketMakingWalletDto> getTransferWalletList(MMWallet mmw, String secretKey) {
		return mmw.getTransferWalletList().stream().peek(mmwd -> {
			mmwd.setPrivateKeyHex(SecurityUtil.decryptString(mmwd.getEncryptedPrivateKey(), secretKey));
		}).collect(Collectors.toList());
	}

	public List<MarketMakingWalletDto> getTransferWalletListByMarketMakingId(long marketMakingId, String secretKey) {
		return findById(marketMakingId).map(mmw -> mmw.getTransferWalletList().stream().peek(
				mmwd -> mmwd.setPrivateKeyHex(SecurityUtil.decryptString(mmwd.getEncryptedPrivateKey(), secretKey)))
				.collect(Collectors.toList())).orElseGet(ArrayList::new);
	}

	public List<MarketMakingWalletDto> getDefiWalletListByMarketMakingId(long marketMakingId, String secretKey) {
		return findById(marketMakingId).map(mmw -> mmw.getDefiWalletList().stream().peek(
				mmwd -> mmwd.setPrivateKeyHex(SecurityUtil.decryptString(mmwd.getEncryptedPrivateKey(), secretKey)))
				.collect(Collectors.toList())).orElseGet(ArrayList::new);
	}

	public List<MarketMakingWalletDto> getDefiWalletList(MMWallet mmw, String secretKey) throws IOException {
		return mmw.getDefiWalletList().stream().peek(mmwd -> {
			mmwd.setPrivateKeyHex(SecurityUtil.decryptString(mmwd.getEncryptedPrivateKey(), secretKey));
		}).collect(Collectors.toList());
	}

	public MarketMakingWalletDto fixMMWallet(MarketMakingWalletDto mmwd, String secretKey) {
		mmwd.setEncryptedPrivateKey(SecurityUtil.encryptString(mmwd.getPrivateKeyHex(), secretKey));
		mmwd.setPrivateKey(null);
		mmwd.setPrivateKeyHex(null);
		return mmwd;
	}

	public List<MMWallet> findAll() {
		return mmWalletDao.findAll();
	}

	public MMWallet save(MMWallet mmw) {
		return mmWalletDao.save(mmw);
	}

}
