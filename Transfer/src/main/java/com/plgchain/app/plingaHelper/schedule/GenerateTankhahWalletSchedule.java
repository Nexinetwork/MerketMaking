package com.plgchain.app.plingaHelper.schedule;

import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.constant.TankhahWalletType;
import com.plgchain.app.plingaHelper.dto.EvmWalletDto;
import com.plgchain.app.plingaHelper.entity.TankhahWallet;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.service.TankhahWalletService;
import com.plgchain.app.plingaHelper.util.blockchain.EvmWalletUtil;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Component
public class GenerateTankhahWalletSchedule implements Serializable {

	private static final long serialVersionUID = 7504602259969637312L;
	private static final Logger logger = LoggerFactory.getLogger(GenerateTankhahWalletSchedule.class);

	private final InitBean initBean;
	private final TankhahWalletService tankhahWalletService;

	@Inject
	public GenerateTankhahWalletSchedule(InitBean initBean, TankhahWalletService tankhahWalletService) {
		this.initBean = initBean;
		this.tankhahWalletService = tankhahWalletService;
	}

	@Scheduled(cron = "0 */10 * * * *", zone = "GMT")
	public void generateTankhahWallet() {
		if (!initBean.doesActionRunning("generateTankhahWallet")) {
			initBean.startActionRunning("generateTankhahWallet");

			List<SmartContract> smartContracts = tankhahWalletService.findAllSmartContractsNotHaveTankhahWallet();
			smartContracts.forEach(smartContract -> {
				try {
					EvmWalletDto w = EvmWalletUtil.generateRandomWallet();
					TankhahWallet tankhahWallet = TankhahWallet.builder().balance(BigDecimal.ZERO).mainCoinBalance(BigDecimal.ZERO)
							.contract(smartContract).privateKey(w.getPrivateKey()).privateKeyHex(w.getHexKey())
							.tankhahWalletType(TankhahWalletType.TRANSFER).publicKey(w.getPublicKey()).build();
					tankhahWallet = tankhahWalletService.save(tankhahWallet);
					logger.info(String.format("Tankhah wallet %s for transfer has been created.", tankhahWallet));
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			});

			initBean.stopActionRunning("generateTankhahWallet");
		} else {
			logger.warn("Schedule method generateTankhahWallet already running, skipping it.");
		}
	}
}
