package com.plgchain.app.plingaHelper.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.plgchain.app.plingaHelper.bean.DefiActionBean;
import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.constant.AdminCommandType;
import com.plgchain.app.plingaHelper.constant.SysConstant;
import com.plgchain.app.plingaHelper.dto.MarketMakingWalletDto;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.microService.BlockchainMicroService;
import com.plgchain.app.plingaHelper.microService.SmartContractMicroService;
import com.plgchain.app.plingaHelper.service.MMWalletService;
import com.plgchain.app.plingaHelper.type.CommandToRun;
import com.plgchain.app.plingaHelper.util.SecurityUtil;
import com.plgchain.app.plingaHelper.util.blockchain.EVMUtil;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 */
// @Component
public class DefiNexiV2SwapSchedule {
	private final static Logger logger = LoggerFactory.getLogger(DefiNexiV2SwapSchedule.class);

	@Inject
	private InitBean initBean;

	@Inject
	private SmartContractMicroService smartContractMicroService;

	@Inject
	private BlockchainMicroService blockchainMicroService;

	@Inject
	private MMWalletService mmWalletService;

	@Inject
	private DefiActionBean defiActionBean;

	@Inject
	private KafkaTemplate<String, String> kafkaTemplate;

	private List<String> minorContractList;
	private List<String> majorContractList;

	private Map<String, List<MarketMakingWalletDto>> minorWalletList = new HashMap<>();
	private Map<String, List<MarketMakingWalletDto>> majorWalletList = new HashMap<>();

	private final String factoryAddress = "0x5f12f78cE81BeA5356f377101d01FB00b1c380Ec";

	private final String routerAddress = "0x4E7e71B45e9429ab7805bdA3552892A6545c0A9e";

	private Blockchain blockchain;

	private final BigDecimal minTokenBalance = new BigDecimal(10);

	private final BigDecimal minfeeBalance = new BigDecimal(0.1);

	private int roundSize = 200;

	@PostConstruct
	@Transactional
	public void init() {
		logger.info("init for DefiNexiV1SwapSchedule has been runned.......");
		this.blockchain = blockchainMicroService.findByName("Nexi-DPOS-V2").get();
		this.minorContractList = Arrays.asList("0x613d19fd91A62513e16Ecc1c0A4bFb16480bd2Bb",
				"0xdF397Aeee4950Aafb7DaD6345747337B510B4951", "0x9032ba5aa0d59888E582E8aa5893b53b07DEceC1",
				"0x1F1FdCf76847E8e9C00048a33dFf1246912a7Fc2", "0x883277f7D623612034db92A2dC16A8BEC20a8FB5",
				"0x040a129440e4d98fABaD86C8A5D291693636c850", "0x0000000000000000000000000000000000001010");
		this.majorContractList = Collections.singletonList("0x40Aa6A2463fBAabEA6DB995aaB604C2393cbc37D");
		this.majorContractList = Arrays.asList("0x40Aa6A2463fBAabEA6DB995aaB604C2393cbc37D","0x0000000000000000000000000000000000001010");

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
		int randomNumber = random.nextInt(4);
		if (randomNumber == 0) {
			return "Major";
		} else if (randomNumber == 1) {
			return "Major";
		} else if (randomNumber == 2) {
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
	// @Scheduled(cron = "0 */5 * * * *", zone = "GMT")
	public void defiNexiV1Swap() {
		if (!initBean.doesActionRunning("defiNexiV1Swap")) {
			initBean.startActionRunning("defiNexiV1Swap");
			logger.info("defiNexiV1Swap started.");

			IntStream.range(0, roundSize).forEach(idx -> {
				String mainContract = "";
				String secondContract = "";
				MarketMakingWalletDto mmwDto = new MarketMakingWalletDto();
				if (selectMajorOrMinor().equals("Major")) {
					mainContract = getRandomMajor();
					secondContract = getRandomMinor();
					mmwDto = getRandomMajorWallet(mainContract);
				} else {
					mainContract = getRandomMinor();
					secondContract = getRandomMajor();
					mmwDto = getRandomMinorWallet(mainContract);
				}
				BigDecimal balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(), mmwDto.getPublicKey());
				if (balance.compareTo(new BigDecimal(0.1)) < 0) {
					CommandToRun ctr = new CommandToRun();
					ctr.setAdminCommandType(AdminCommandType.FUNDACCOUNTFORCONTRACT);
					ctr.setLong1(mmwDto.getContractId());
					ctr.setStr1(mmwDto.getPrivateKeyHex());
					ctr.setStr1(mmwDto.getPublicKey());
					kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
				} else {
					logger.info(String.format("%s/%s) try to swap between %s ----> %s for wallet %s", idx + 1, roundSize,
							mainContract, secondContract,mmwDto.getPublicKey()));
					if (mainContract.equals(EVMUtil.mainToken)) {

					} else if (secondContract.equals(EVMUtil.mainToken)) {

					} else {
						BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(),
								mmwDto.getPrivateKeyHex(), mainContract);
						if (tokenBalance.compareTo(minTokenBalance) < 0) {
							CommandToRun ctr = new CommandToRun();
							ctr.setAdminCommandType(AdminCommandType.FUNDACCOUNTFORCONTRACT);
							ctr.setLong1(mmwDto.getContractId());
							ctr.setStr1(mmwDto.getPrivateKeyHex());
							ctr.setStr2(mmwDto.getPublicKey());
							ctr.setBigDecimal1(minTokenBalance.multiply(new BigDecimal(5)));
							kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
						} else if (balance.compareTo(minfeeBalance) < 0) {
							CommandToRun ctr = new CommandToRun();
							ctr.setAdminCommandType(AdminCommandType.FUNDACCOUNTFORCONTRACT);
							ctr.setLong1(mmwDto.getContractId());
							ctr.setStr1(mmwDto.getPrivateKeyHex());
							ctr.setStr2(mmwDto.getPublicKey());
							ctr.setBigDecimal1(minfeeBalance);
							kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
						} else {
							defiActionBean.swapTokenByToken(blockchain.getRpcUrl(), routerAddress, factoryAddress,
									mmwDto.getPrivateKeyHex(), mainContract, secondContract,
									defiActionBean.calcRandomPercentInBigDecimal(balance, new BigDecimal(10), new BigDecimal(20),2));
						}
					}

				}
			});

			initBean.stopActionRunning("defiNexiV1Swap");
			logger.info("defiNexiV1Swap finished.");
		} else

		{
			logger.warn("Schedule method defiNexiV1Swap already running, skipping it.");
		}
	}
}
