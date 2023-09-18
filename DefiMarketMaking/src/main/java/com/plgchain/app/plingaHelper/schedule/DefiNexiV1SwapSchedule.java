package com.plgchain.app.plingaHelper.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.dto.MarketMakingWalletDto;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.microService.BlockchainMicroService;
import com.plgchain.app.plingaHelper.microService.SmartContractMicroService;
import com.plgchain.app.plingaHelper.service.MMWalletService;
import com.plgchain.app.plingaHelper.util.SecurityUtil;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Component
public class DefiNexiV1SwapSchedule {
	private final static Logger logger = LoggerFactory.getLogger(DefiNexiV1SwapSchedule.class);

	@Inject
	private InitBean initBean;

	@Inject
	private SmartContractMicroService smartContractMicroService;

	@Inject
	private BlockchainMicroService blockchainMicroService;

	@Inject
	private MMWalletService mmWalletService;

	private List<String> minorContractList;
	private List<String> majorContractList;

	private Map<String, List<MarketMakingWalletDto>> minorWalletList = new HashMap<>();
	private Map<String, List<MarketMakingWalletDto>> majorWalletList = new HashMap<>();

	private Blockchain blockchain;

	@PostConstruct
	@Transactional
	public void init() {
		logger.info("init for DefiNexiV1SwapSchedule has been runned.......");
		this.blockchain = blockchainMicroService.findByName("Nexi-DPOS-V1").orElse(null);
		this.minorContractList = Arrays.asList("0x613d19fd91A62513e16Ecc1c0A4bFb16480bd2Bb",
				"0xdF397Aeee4950Aafb7DaD6345747337B510B4951", "0x9032ba5aa0d59888E582E8aa5893b53b07DEceC1",
				"0x1F1FdCf76847E8e9C00048a33dFf1246912a7Fc2", "0x883277f7D623612034db92A2dC16A8BEC20a8FB5",
				"0x040a129440e4d98fABaD86C8A5D291693636c850", "0x0000000000000000000000000000000000001010");
		this.majorContractList = Collections.singletonList("0x40Aa6A2463fBAabEA6DB995aaB604C2393cbc37D");

		majorContractList.forEach(this::initializeMajorWalletList);
		minorContractList.forEach(this::initializeMinorWalletList);
	}

	private void initializeMajorWalletList(String contractAddress) {
		smartContractMicroService.findByBlockchainAndContractsAddress(blockchain, contractAddress).ifPresent(sm -> {
			mmWalletService.findByContractIdAndChunk(sm.getContractId(), 0).ifPresent(mmw -> {
				this.majorWalletList.put(contractAddress, mmw.getDefiWalletList().stream()
						.peek(wallet -> wallet.setPrivateKeyHex(SecurityUtil
								.decryptString(wallet.getEncryptedPrivateKey(), sm.getMarketMakingObject().getTrPid())))
						.collect(Collectors.toList()));
			});
		});
	}

	private void initializeMinorWalletList(String contractAddress) {
		smartContractMicroService.findByBlockchainAndContractsAddress(blockchain, contractAddress).ifPresent(sm -> {
			mmWalletService.findByContractIdAndChunk(sm.getContractId(), 0).ifPresent(mmw -> {
				this.minorWalletList.put(contractAddress, mmw.getDefiWalletList().stream()
						.peek(wallet -> wallet.setPrivateKeyHex(SecurityUtil
								.decryptString(wallet.getEncryptedPrivateKey(), sm.getMarketMakingObject().getTrPid())))
						.collect(Collectors.toList()));
			});
		});
	}

	public String selectMajorOrMinor() {
		Random random = new Random();
		int randomNumber = random.nextInt(2);
		if (randomNumber == 0) {
			return "Major";
		} else {
			return "Minor";
		}
	}

	private String getRandomMajor() {
		Random random = new Random();
		int randomNumber = random.nextInt(majorContractList.size());
		return this.majorContractList.get(randomNumber);
	}

	private MarketMakingWalletDto getRandomMinorWallet(String contract) {
		Random random = new Random();
		int randomNumber = random.nextInt(minorWalletList.get(contract).size());
		return this.minorWalletList.get(contract).get(randomNumber);
	}

	private MarketMakingWalletDto getRandomMajorWallet(String contract) {
		Random random = new Random();
		int randomNumber = random.nextInt(majorWalletList.get(contract).size());
		return this.majorWalletList.get(contract).get(randomNumber);
	}

	private String getRandomMinor() {
		Random random = new Random();
		int randomNumber = random.nextInt(minorContractList.size());
		return this.minorContractList.get(randomNumber);
	}

	@Transactional
	@Scheduled(cron = "0 */5 * * * *", zone = "GMT")
	public void defiNexiV1Swap() {
		if (!initBean.doesActionRunning("defiNexiV1Swap")) {
			initBean.startActionRunning("defiNexiV1Swap");
			logger.info("defiNexiV1Swap started.");

			// Your business logic goes here

			initBean.stopActionRunning("defiNexiV1Swap");
			logger.info("defiNexiV1Swap finished.");
		} else {
			logger.warn("Schedule method defiNexiV1Swap already running, skipping it.");
		}
	}
}
