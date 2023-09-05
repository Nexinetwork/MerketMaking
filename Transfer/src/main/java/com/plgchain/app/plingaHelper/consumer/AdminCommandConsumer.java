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
import com.plgchain.app.plingaHelper.bean.InitBean;
import com.plgchain.app.plingaHelper.bean.WalletActionBean;
import com.plgchain.app.plingaHelper.constant.AdminCommandType;
import com.plgchain.app.plingaHelper.constant.SysConstant;
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
				} else if (ctr.getAdminCommandType().equals(AdminCommandType.BACKALLTOKENTOTANKHAH)) {
					walletActionBean.backAllTokenToTankhah(ctr.getLong1());
				} else if (ctr.getAdminCommandType().equals(AdminCommandType.BACKALLFROMTMPTANKHAHTOTANKHAH)) {
					walletActionBean.backAllTokenFromTempTankhahToTankhah(ctr.getLong1());
				} else if (ctr.getAdminCommandType().equals(AdminCommandType.DELETETEMPTANKHAHWALLET)) {
					walletActionBean.deleteTempTankhahWallet(ctr.getLong1());
				}
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		}

	}

}
