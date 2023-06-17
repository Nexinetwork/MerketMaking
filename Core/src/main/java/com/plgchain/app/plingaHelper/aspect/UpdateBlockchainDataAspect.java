package com.plgchain.app.plingaHelper.aspect;

import java.io.Serializable;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import com.alibaba.fastjson2.JSON;
import com.plgchain.app.plingaHelper.constant.AdminCommandType;
import com.plgchain.app.plingaHelper.constant.SysConstant;
import com.plgchain.app.plingaHelper.type.CommandToRun;

public class UpdateBlockchainDataAspect implements Serializable{

	private static final long serialVersionUID = 7574366958683551337L;

	private final static Logger logger = LoggerFactory.getLogger(UpdateBlockchainDataAspect.class);

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Pointcut("@within(com.plgchain.app.plingaHelper.annotation.UpdateBlockchainData)")
	public void updateBlockchainData() {
	}

	@AfterReturning(pointcut = "(updateBlockchainData()")
	public void adviceAfterReturning(JoinPoint jp) {
		logger.info("Update BlockchainData has been invoked.");

		CommandToRun ctr = new CommandToRun();
		ctr.setAdminCommandType(AdminCommandType.UPDATEBLOCKCHAIN);
		kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
	}

}
