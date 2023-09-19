/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import com.netflix.servo.util.Strings;
import com.plgchain.app.plingaHelper.contracts.defi.nexi.v1.NexiSwapFactory;
import com.plgchain.app.plingaHelper.contracts.defi.nexi.v1.NexiSwapRouter;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.microService.SmartContractMicroService;
import com.plgchain.app.plingaHelper.service.MMWalletService;
import com.plgchain.app.plingaHelper.util.SecurityUtil;
import com.plgchain.app.plingaHelper.util.blockchain.EVMUtil;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 *
 */
@Service
public class DefiActionBean implements Serializable {

	private static final long serialVersionUID = 8374866810390080678L;

	private static final Logger logger = LoggerFactory.getLogger(DefiActionBean.class);

	private final InitBean initBean;
	private final MMWalletService mmWalletService;
	private final SmartContractMicroService smartContractMicroService;

	@Inject
	public DefiActionBean(InitBean initBean, MMWalletService mmWalletService,
			SmartContractMicroService smartContractMicroService) {
		this.initBean = initBean;
		this.mmWalletService = mmWalletService;
		this.smartContractMicroService = smartContractMicroService;
	}

	@Async
	@Transactional
	public void approveDefiV1WalletOfContractforOtherContract(long contractId, String contractAddress) {
		var sm = smartContractMicroService.findById(contractId).get();
		Blockchain blockchain = sm.getBlockchain();
		// Coin coin = sm.getCoin();
		if (Strings.isNullOrEmpty(blockchain.getDefiV1Factory())) {
			logger.info(String.format("factory for defi V1 for blockchain %s not set.", blockchain.getName()));
			return;
		}
		if (Strings.isNullOrEmpty(blockchain.getDefiV1Router01())) {
			logger.info(String.format("router01 for defi V1 for blockchain %s not set.", blockchain.getName()));
			return;
		}
		if (Strings.isNullOrEmpty(blockchain.getDefiV1Router())) {
			logger.info(String.format("router for defi V1 for blockchain %s not set.", blockchain.getName()));
			return;
		}

		mmWalletService.findByContractIdAndChunk(contractId, 0).ifPresent(mmw -> {
			var wList = mmw.getDefiWalletList();
			logger.info(
					String.format("Defi wallet count for contract %s is %s", sm.getContractsAddress(), wList.size()));
			mmw.getDefiWalletList().forEach(wallet -> {

				wallet.setPrivateKeyHex(SecurityUtil.decryptString(wallet.getEncryptedPrivateKey(),
						sm.getMarketMakingObject().getTrPid()));

				if (blockchain.isAutoGas()) {

				} else {
					// logger.info("Try to approve wallet : " + wallet);
					TransactionReceipt tr = EVMUtil.approveContractOnWallet(blockchain.getRpcUrl(),
							wallet.getPrivateKeyHex(), blockchain.getDefiV1Router(), contractAddress);
					if (tr != null) {
						if (!Strings.isNullOrEmpty(tr.getTransactionHash())) {
							logger.info(String.format(
									"Contract %s has been approved for token %s on blockchain %s with txhash %s",
									blockchain.getDefiV1Router(), contractAddress, blockchain.getName(),
									tr.getTransactionHash()));
						}
					}
				}

			});
		});

	}

	@Async
	@Transactional
	public void swapTokenByToken(String rpcUrl, String nexiRouter, String nexiFactory, String privateKey,
			String contract1, String contract2, BigDecimal amount) {
		LocalDateTime fue = LocalDateTime.now().plusHours(1);
		long milliSeconds = Timestamp.valueOf(fue).getTime();
		Web3j web3j = Web3j.build(new HttpService(rpcUrl));
		Credentials credentials = Credentials.create(privateKey);
		NexiSwapRouter nsr = NexiSwapRouter.load(nexiRouter, web3j, credentials, new DefaultGasProvider());
		if (!doesPairExist(rpcUrl, nexiFactory, privateKey, contract1, contract2)) {
			logger.info(String.format("Pair %s with %s does not exist ignore swap", contract1, contract2));
			if (!contract1.equals(EVMUtil.mainToken)) {
				approveDefiV1WalletOfContractforOtherContract(rpcUrl, nexiRouter, privateKey, contract1);
			}
			if (!contract2.equals(EVMUtil.mainToken)) {
				approveDefiV1WalletOfContractforOtherContract(rpcUrl, nexiRouter, privateKey, contract2);
			}
			return;
		}
		boolean mustRetry = true;
		while (mustRetry) {
			try {
				BigInteger amountOut = (BigInteger) nsr
						.getAmountsOut(Convert.toWei(amount.toString(), Convert.Unit.ETHER).toBigInteger(),
								Arrays.asList(contract1, contract2))
						.send().get(0);
				TransactionReceipt transactionReceipt = nsr
						.swapExactTokensForTokens(Convert.toWei(amount.toString(), Convert.Unit.ETHER).toBigInteger(),
								calcPercentInBigDecimal(amount, new BigDecimal("0.0000000001")).toBigInteger(),
								Arrays.asList(contract1, contract2), credentials.getAddress(),
								BigInteger.valueOf(milliSeconds))
						.send();
				logger.info(String.format("Swap between %s with %s with amount %s with txhash %s has been done.",
						contract1, contract2, amount, transactionReceipt.getTransactionHash()));
				mustRetry = false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				if (!contract1.equals(EVMUtil.mainToken)) {
					approveDefiV1WalletOfContractforOtherContract(rpcUrl, nexiRouter, privateKey, contract1);
				}
				if (!contract2.equals(EVMUtil.mainToken)) {
					approveDefiV1WalletOfContractforOtherContract(rpcUrl, nexiRouter, privateKey, contract2);
				}
			}
		}

	}

	public void approveDefiV1WalletOfContractforOtherContract(String rpcUrl, String routerAddress, String privateKey,
			String contractAddress) {
		EVMUtil.approveContractOnWallet(rpcUrl, privateKey, routerAddress, contractAddress);
	}

	public boolean doesPairExist(String rpcUrl, String nexiFactory, String privKey, String cointract1,
			String contract2) {
		Web3j web3j = Web3j.build(new HttpService(rpcUrl));
		Credentials credentials = Credentials.create(privKey);
		NexiSwapFactory nexiSwapFactory = NexiSwapFactory.load(nexiFactory, web3j, credentials,
				new DefaultGasProvider());
		try {
			return !nexiSwapFactory.getPair(cointract1, contract2).send()
					.contains("0x0000000000000000000000000000000000000000");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public String getPairAddress(String rpcUrl, String nexiFactory, String privKey, String cointract1,
			String contract2) {
		Web3j web3j = Web3j.build(new HttpService(rpcUrl));
		Credentials credentials = Credentials.create(privKey);
		NexiSwapFactory nexiSwapFactory = NexiSwapFactory.load(nexiFactory, web3j, credentials,
				new DefaultGasProvider());
		try {
			return nexiSwapFactory.getPair(cointract1, contract2).send();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "0x0000000000000000000000000000000000000000";
	}

	public BigDecimal calcPercentInBigDecimal(BigDecimal amount, BigDecimal percent) {
		return amount.multiply(percent).scaleByPowerOfTen(-2);
	}

	public BigDecimal calcRandomPercentInBigDecimal(BigDecimal amount, BigDecimal percent1, BigDecimal percent2, int decimal) {
        Random random = new Random();
        BigDecimal randomBigDecimal = percent1.add(BigDecimal.valueOf(random.nextDouble()).multiply(percent2.subtract(percent1)));
        return randomBigDecimal.setScale(decimal, RoundingMode.HALF_UP).multiply(amount).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
    }

}
