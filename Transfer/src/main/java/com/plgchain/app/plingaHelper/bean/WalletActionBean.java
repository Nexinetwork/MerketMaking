package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dto.MarketMakingWalletDto;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.exception.InvalidMarketMaking;
import com.plgchain.app.plingaHelper.service.MarketMakingService;
import com.plgchain.app.plingaHelper.service.MarketMakingWalletService;
import com.plgchain.app.plingaHelper.service.SmartContractService;
import com.plgchain.app.plingaHelper.service.TankhahWalletService;
import com.plgchain.app.plingaHelper.util.NumberUtil;
import com.plgchain.app.plingaHelper.util.blockchain.EVMUtil;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@Service
public class WalletActionBean implements Serializable {

	private static final long serialVersionUID = -8145289049266025010L;

	private static final Logger logger = LoggerFactory.getLogger(WalletActionBean.class);

	private final InitBean initBean;
	private final MarketMakingService marketMakingService;
	private final TransferBean transferBean;
	private final TankhahWalletService tankhahWalletService;
	private final MarketMakingWalletService mmWalletService;
	private final SmartContractService smartContractService;

	private final int sleepInSeconds = 3;

	@Inject
	public WalletActionBean(InitBean initBean, MarketMakingService marketMakingService,
			TransferBean transferBean, TankhahWalletService tankhahWalletService,
			MarketMakingWalletService mmWalletService,SmartContractService smartContractService) {
		this.initBean = initBean;
		this.marketMakingService = marketMakingService;
		this.transferBean = transferBean;
		this.tankhahWalletService = tankhahWalletService;
		this.mmWalletService = mmWalletService;
		this.smartContractService = smartContractService;
	}

	@Async
	@Transactional
	public void fixAllTransferWalletsByContractId(long contractId) {
		var sm = smartContractService.findById(contractId).get();
		Blockchain blockchain = sm.getBlockchain();
		Coin coin = sm.getCoin();
		var tankhahWallet = tankhahWalletService.findByContract(sm).get(0);
		var mm = marketMakingService.findBySmartContract(sm).get();
		if (!(mm.isInitialWalletCreationDone() && mm.isInitialWalletFundingDone()))
			throw new InvalidMarketMaking();
		final int[] enqueued = { 0 };
		final BigInteger[] tankhahNonce = {
				EVMUtil.getNonce(blockchain.getRpcUrl(), tankhahWallet.getPrivateKeyHex()) };
		logger.info(String.format(
				"Nonce for wallet %s of Contract address %s and coin %s and blockchain %s is %s",
				tankhahWallet.getPublicKey(), sm.getContractsAddress(), coin.getSymbol(),
				blockchain.getName(), tankhahNonce[0]));
		int page = 0;
		Page<MarketMakingWalletDto> mmWalletPageDto = null;
		do {
			PageRequest pageable = PageRequest.of(page,
					initBean.getFixTransferWalletBalancePerRound());
			mmWalletPageDto = initBean
					.getMMWalletList(sm.getContractId(), pageable);
			mmWalletPageDto.stream().forEach(wallet -> {
				if (sm.getContractsAddress().equals(EVMUtil.mainToken)) {
					BigDecimal balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
							wallet.getPublicKey());
					if (balance.compareTo(mm.getMaxInitial()) > 0) {
						var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(),
								mm.getMaxInitial(), mm.getInitialDecimal());
						var mustReturn = balance.subtract(amount);
						BigInteger nonce = EVMUtil.getNonce(blockchain.getRpcUrl(),
								wallet.getPrivateKeyHex());
						if (blockchain.isAutoGas()) {
							transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
									wallet.getPrivateKeyHex(), wallet.getPublicKey(),
									tankhahWallet.getPublicKey(), mustReturn,
									EVMUtil.DefaultGasLimit, nonce);
						} else {
							transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
									wallet.getPrivateKeyHex(), wallet.getPublicKey(),
									tankhahWallet.getPublicKey(), mustReturn,
									EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit, nonce);
						}
					} else if (balance.equals(BigDecimal.ZERO)) {
						var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(),
								mm.getMaxInitial(), mm.getInitialDecimal());
						if (blockchain.isAutoGas()) {
							transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
									tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
									wallet.getPublicKey(), amount, EVMUtil.DefaultGasLimit,
									tankhahNonce[0]);
						} else {
							transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
									tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
									wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,
									EVMUtil.DefaultGasLimit, tankhahNonce[0]);
						}
						tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
						enqueued[0]++;
						if (enqueued[0] >= 100) {
							try {
								Thread.sleep(sleepInSeconds * 1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							enqueued[0] = 0;
						}
					}
				} else {
					BigDecimal balance = EVMUtil.getAccountBalance(blockchain.getRpcUrl(),
							wallet.getPublicKey());

					BigDecimal tokenBalance = EVMUtil.getTokenBalancSync(blockchain.getRpcUrl(),
							wallet.getPrivateKeyHex(), sm.getContractsAddress());
					if (balance.compareTo(initBean.getMaxMaincoinInContractWallet()) > 0) {
						var amount = NumberUtil.generateRandomNumber(
								initBean.getMinMaincoinInContractWallet(),
								initBean.getMaxMaincoinInContractWallet(),
								initBean.getDecimalMaincoinInContractWallet());
						var mustReturn = balance.subtract(amount);
						BigInteger nonce = EVMUtil.getNonce(blockchain.getRpcUrl(),
								wallet.getPrivateKeyHex());
						if (blockchain.isAutoGas()) {
							transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
									wallet.getPrivateKeyHex(), wallet.getPublicKey(),
									tankhahWallet.getPublicKey(), mustReturn,
									EVMUtil.DefaultGasLimit, nonce);
						} else {
							transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
									wallet.getPrivateKeyHex(), wallet.getPublicKey(),
									tankhahWallet.getPublicKey(), mustReturn,
									EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit, nonce);
						}
					} else if (balance.equals(BigDecimal.ZERO)) {
						var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(),
								mm.getMaxInitial(), mm.getInitialDecimal());
						if (blockchain.isAutoGas()) {
							transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
									tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
									wallet.getPublicKey(), amount, EVMUtil.DefaultGasLimit,
									tankhahNonce[0]);
						} else {
							transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
									tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
									wallet.getPublicKey(), amount, EVMUtil.DefaultGasPrice,
									EVMUtil.DefaultGasLimit, tankhahNonce[0]);
						}
						tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
						enqueued[0]++;
						if (enqueued[0] >= 100) {
							try {
								Thread.sleep(sleepInSeconds * 1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							enqueued[0] = 0;
						}
					}
					var amount = NumberUtil.generateRandomNumber(mm.getMinInitial(),
							mm.getMaxInitial(), mm.getInitialDecimal());
					if (tokenBalance.compareTo(mm.getMaxInitial()) > 0) {
						var mustReturn = tokenBalance.subtract(amount);
						BigInteger nonce = EVMUtil.getNonce(blockchain.getRpcUrl(),
								wallet.getPrivateKeyHex());
						if (blockchain.isAutoGas()) {
							transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
									wallet.getPrivateKeyHex(), wallet.getPublicKey(),
									tankhahWallet.getPublicKey(), sm.getContractsAddress(),
									mustReturn, EVMUtil.DefaultTokenGasLimit, nonce);
						} else {
							transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
									wallet.getPrivateKeyHex(), wallet.getPublicKey(),
									tankhahWallet.getPublicKey(), sm.getContractsAddress(),
									mustReturn, EVMUtil.DefaultGasPrice,
									EVMUtil.DefaultTokenGasLimit, nonce);
						}
					} else if (tokenBalance.equals(BigDecimal.ZERO)) {
						if (blockchain.isAutoGas()) {
							transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
									tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
									wallet.getPublicKey(), sm.getContractsAddress(), amount,
									EVMUtil.DefaultTokenGasLimit, tankhahNonce[0]);
						} else {
							transferBean.transferBetweenToAccount(blockchain.getRpcUrl(),
									tankhahWallet.getPrivateKeyHex(), tankhahWallet.getPublicKey(),
									wallet.getPublicKey(), sm.getContractsAddress(), amount,
									EVMUtil.DefaultGasPrice, EVMUtil.DefaultTokenGasLimit,
									tankhahNonce[0]);
						}
						tankhahNonce[0] = tankhahNonce[0].add(BigInteger.ONE);
						enqueued[0]++;
						if (enqueued[0] >= 100) {
							try {
								Thread.sleep(sleepInSeconds * 1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							enqueued[0] = 0;
						}
					}
				}
			});
			page++;
		} while (mmWalletPageDto.hasNext());

	}

}
