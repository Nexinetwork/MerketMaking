/**
 *
 */
package com.plgchain.app.plingaHelper.consumer;

import java.io.Serializable;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.plgchain.app.plingaHelper.bean.ContractActionBean;
import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.bean.WalletActionBean;
import com.plgchain.app.plingaHelper.constant.AdminCommandType;
import com.plgchain.app.plingaHelper.constant.SysConstant;
import com.plgchain.app.plingaHelper.service.SmartContractService;
import com.plgchain.app.plingaHelper.type.CommandToRun;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
/**
 *
 */
@Component
@Slf4j
public class AdminCommandConsumer implements Serializable {

	private static final long serialVersionUID = 8556636738432380764L;
	private final static Logger logger = LoggerFactory.getLogger(AdminCommandConsumer.class);

	@Autowired
	private InitBean initBean;

	@Inject
	private WalletActionBean walletActionBean;

	@Inject
	private ContractActionBean contractActionBean;

	@KafkaListener(topics = SysConstant.KAFKA_ADMIN_COMMAND, containerFactory = "kafkaListenerContainerFactory", groupId = "TransferService.handleAdminCommand")
	public void handleAdminCommand(List<ConsumerRecord<String, String>> records) {
		try {
			for (int i = 0; i < records.size(); i++) {
				ConsumerRecord<String, String> record = records.get(i);
				logger.info("New message is : " + record.value());
				CommandToRun ctr = JSON.parseObject(record.value(), CommandToRun.class);
				if (ctr.getAdminCommandType().equals(AdminCommandType.FIXTRANSFERWALLETFUNDING)) {
					//walletActionBean.generateTempTankhahWallet(ctr.getLong1());
					walletActionBean.fixAllTransferWalletsByContractIdInOneActionWithTempTankhah(ctr.getLong1());
				} else if (ctr.getAdminCommandType().equals(AdminCommandType.FIXTRANSFERWALLETFUNDINGREVERSE)) {
					walletActionBean.fixAllTransferWalletsByContractIdInOneActionWithTempTankhahReverse(ctr.getLong1());
				} else if (ctr.getAdminCommandType().equals(AdminCommandType.BACKALLTOKENTOTANKHAH)) {
					if (ctr.getInt1() == null)
						walletActionBean.backAllTokenToTankhah(ctr.getLong1(),0);
					else if (ctr.getInt1() <= 0)
						walletActionBean.backAllTokenToTankhah(ctr.getLong1(),0);
					else
						walletActionBean.backAllTokenToTankhah(ctr.getLong1(),ctr.getInt1());
				} else if (ctr.getAdminCommandType().equals(AdminCommandType.BACKALLTOKENTOTANKHAHPPARALLEL)) {
					if (ctr.getInt1() == null)
						walletActionBean.backAllTokenToTankhahParallel(ctr.getLong1(),0);
					else if (ctr.getInt1() <= 0)
						walletActionBean.backAllTokenToTankhahParallel(ctr.getLong1(),0);
					else
						walletActionBean.backAllTokenToTankhahParallel(ctr.getLong1(),ctr.getInt1());
				} else if (ctr.getAdminCommandType().equals(AdminCommandType.CREDITMAINCOINBALANCEINTOKENWALLETS)) {
					if (ctr.getInt1() == null)
						walletActionBean.creditMinimumMainCoinForTokenWallets(ctr.getLong1(),0);
					else if (ctr.getInt1() <= 0)
						walletActionBean.creditMinimumMainCoinForTokenWallets(ctr.getLong1(),0);
					else
						walletActionBean.creditMinimumMainCoinForTokenWallets(ctr.getLong1(),ctr.getInt1());
				} else if (ctr.getAdminCommandType().equals(AdminCommandType.CREDITMAINCOINBALANCEINTOKENWALLETSREVERSE)) {
					if (ctr.getInt1() == null)
						walletActionBean.creditMinimumMainCoinForTokenWalletsReverse(ctr.getLong1(),0);
					else if (ctr.getInt1() <= 0)
						walletActionBean.creditMinimumMainCoinForTokenWalletsReverse(ctr.getLong1(),0);
					else
						walletActionBean.creditMinimumMainCoinForTokenWalletsReverse(ctr.getLong1(),ctr.getInt1());
				} else if (ctr.getAdminCommandType().equals(AdminCommandType.BACKALLTOKENTOTANKHAHREVERSE)) {
					walletActionBean.backAllTokenToTankhahReverse(ctr.getLong1());
				} else if (ctr.getAdminCommandType().equals(AdminCommandType.BACKALLFROMTMPTANKHAHTOTANKHAH)) {
					walletActionBean.backAllTokenFromTempTankhahToTankhah(ctr.getLong1());
				} else if (ctr.getAdminCommandType().equals(AdminCommandType.DELETETEMPTANKHAHWALLET)) {
					walletActionBean.deleteTempTankhahWallet(ctr.getLong1());
				} else if (ctr.getAdminCommandType().equals(AdminCommandType.UPDATEALLWALLETSBALANCESBYCONTRACTID)) {
					walletActionBean.updateAllwalletsBalancesByContractId(ctr.getLong1());
				} else if (ctr.getAdminCommandType().equals(AdminCommandType.UPDATEALLWALLETSBALANCESBYCONTRACTIDPARALLEL)) {
					walletActionBean.updateAllwalletsBalancesByContractIdParallel(ctr.getLong1());
				} else if (ctr.getAdminCommandType().equals(AdminCommandType.UPDATECONTRACTADDRESS)) {
					contractActionBean.changeContractAddressOfContract(ctr.getLong1(),ctr.getStr1());
				}
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		}

	}

}
