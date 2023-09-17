/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.netflix.servo.util.Strings;
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
		//Coin coin = sm.getCoin();
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
			logger.info(String.format("Defi wallet count for contract %s is %s", sm.getContractsAddress(),wList.size()));
			mmw.getDefiWalletList().forEach(wallet -> {
				wallet.setPrivateKeyHex(SecurityUtil.decryptString(wallet.getEncryptedPrivateKey(),
						sm.getMarketMakingObject().getTrPid()));
				if (blockchain.isAutoGas()) {

				} else {
					logger.info("Try to approve wallet : " + wallet);
					TransactionReceipt tr = EVMUtil.approveContractOnWallet(blockchain.getRpcUrl(),
							wallet.getPrivateKeyHex(), blockchain.getDefiV1Router(), contractAddress,
							EVMUtil.DefaultGasPrice, EVMUtil.DefaultGasLimit);
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

}
