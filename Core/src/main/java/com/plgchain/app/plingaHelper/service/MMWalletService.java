package com.plgchain.app.plingaHelper.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.dto.MarketMakingWalletDto;
import com.plgchain.app.plingaHelper.entity.MMWallet;
import com.plgchain.app.plingaHelper.microService.MMWalletMicroService;
import com.plgchain.app.plingaHelper.util.SecurityUtil;

import jakarta.inject.Inject;

@Service
public class MMWalletService {

	@Inject
	private MMWalletMicroService mmWalletMicroService;

	public void deleteByMarketMakingId(long marketMakingId) {
		mmWalletMicroService.deleteByMarketMakingId(marketMakingId);
	}

	public void deleteByContractId(long contractId) {
		mmWalletMicroService.deleteByContractId(contractId);
	}

	public List<MMWallet> findByContractId(long contractId) {
		return mmWalletMicroService.findByContractId(contractId);
	}

	public Optional<MMWallet> findByContractIdAndChunk(long contractId,long chunk) {
		return mmWalletMicroService.findByContractIdAndChunk(contractId,chunk);
	}

	public MMWallet setTransferWallet(MMWallet mmw, List<MarketMakingWalletDto> mmwdNewLst, String secretKey) {
		List<MarketMakingWalletDto> mmwdlLst = new ArrayList<MarketMakingWalletDto>();
		mmwdlLst.addAll(mmwdNewLst.stream().map(mmwd -> {
			mmwd = fixMMWallet(mmwd, secretKey);
			mmwd.setCoinId(mmw.getCoinId());
			mmwd.setBlockchain(mmw.getBlockchain());
			mmwd.setCoin(mmw.getCoin());
			mmwd.setCoinSymbol(mmw.getCoinSymbol());
			mmwd.setBlockchainId(mmw.getBlockchainId());
			mmwd.setContractId(mmw.getContractId());
			mmwd.setWalletType(WalletType.TRANSFER);
			return mmwd;
		}).collect(Collectors.toList()));
		mmw.setTransferWalletList(mmwdlLst);
		return mmw;
	}

	public MMWallet setDefiWallet(MMWallet mmw, List<MarketMakingWalletDto> mmwdNewLst, String secretKey) {
		List<MarketMakingWalletDto> mmwdlLst = new ArrayList<MarketMakingWalletDto>();
		mmwdlLst.addAll(mmwdNewLst.stream().map(mmwd -> {
			mmwd = fixMMWallet(mmwd, secretKey);
			mmwd.setCoinId(mmw.getCoinId());
			mmwd.setBlockchain(mmw.getBlockchain());
			mmwd.setCoin(mmw.getCoin());
			mmwd.setCoinSymbol(mmw.getCoinSymbol());
			mmwd.setBlockchainId(mmw.getBlockchainId());
			mmwd.setContractId(mmw.getContractId());
			mmwd.setWalletType(WalletType.TRANSFER);
			return mmwd;
		}).collect(Collectors.toList()));
		mmw.setTransferWalletList(mmwdlLst);
		return mmw;
	}

	public MarketMakingWalletDto fixMMWallet(MarketMakingWalletDto mmwd, String secretKey) {
		mmwd.setEncryptedPrivateKey(SecurityUtil.encryptString(mmwd.getPrivateKeyHex(), secretKey));
		mmwd.setPrivateKey(null);
		mmwd.setPrivateKeyHex(null);
		return mmwd;
	}

	public List<MMWallet> saveWithChunk(MMWallet mmw, int chunkSize, int defiWalletSize) {
		List<MarketMakingWalletDto> transferWalletList = mmw.getTransferWalletList();
		int chunkCount = (int) Math.ceil((double) transferWalletList.size() / chunkSize);

		List<MMWallet> chunkedData = IntStream.range(0, chunkCount).mapToObj(i -> {
			List<MarketMakingWalletDto> chunkedTransferList = transferWalletList.stream().skip(i * chunkSize)
					.limit(chunkSize).collect(Collectors.toList());

			List<MarketMakingWalletDto> shuffledDefiWalletList = new ArrayList<>(mmw.getTransferWalletList());
			Collections.shuffle(shuffledDefiWalletList);

			MMWallet mv = new MMWallet(mmw);
			mv.setChunk(i);
			mv.setTransferWalletList(chunkedTransferList);

			if (i == 0) {
				mv.setDefiWalletList(
						shuffledDefiWalletList.stream().limit(defiWalletSize).collect(Collectors.toList()));
			}

			return mv;
		}).map(mmWalletMicroService::save).collect(Collectors.toList());

		return chunkedData;
	}

	public List<MMWallet> saveWithChunk(MMWallet mmw, List<MarketMakingWalletDto> transferWalletList, int chunkSize,
			int defiWalletSize, String secretKey) {
		mmw = setTransferWallet(mmw, transferWalletList, secretKey);
		return saveWithChunk(mmw, chunkSize, defiWalletSize);
	}

}
