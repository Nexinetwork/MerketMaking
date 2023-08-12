package com.plgchain.app.plingaHelper.controller.godController;

import java.io.Serializable;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSON;
import com.plgchain.app.plingaHelper.constant.AdminCommandType;
import com.plgchain.app.plingaHelper.constant.SysConstant;
import com.plgchain.app.plingaHelper.controller.BaseController;
import com.plgchain.app.plingaHelper.entity.SystemConfig;
import com.plgchain.app.plingaHelper.service.SystemConfigService;
import com.plgchain.app.plingaHelper.type.CommandToRun;
import com.plgchain.app.plingaHelper.util.MessageResult;

import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/godaction/config")
@RequiredArgsConstructor
public class ConfigController extends BaseController implements Serializable {

	private static final long serialVersionUID = -2075389879202275735L;

	private final static Logger logger = LoggerFactory.getLogger(ConfigController.class);

	@Inject
	private SystemConfigService systemConfigService;

	@Inject
	private KafkaTemplate<String, String> kafkaTemplate;

	@RequestMapping("/enableHealchCheck")
	public MessageResult enableHealchCheck() {
		try {
            String configName = "checkNodeHealth";

            Optional<SystemConfig> existingConfig = systemConfigService.findByConfigName(configName);

            SystemConfig sc = existingConfig.orElseGet(() -> new SystemConfig(configName));
            sc.setConfigBooleanValue(true);
            systemConfigService.save(sc);
            CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.RELOADCONFIGS);
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
            return success("Blockchain health check has been enabled.");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return error(e.getMessage());
        }
	}

	@RequestMapping("/disableHealchCheck")
	public MessageResult disableHealchCheck() {
		try {
            String configName = "checkNodeHealth";

            Optional<SystemConfig> existingConfig = systemConfigService.findByConfigName(configName);

            SystemConfig sc = existingConfig.orElseGet(() -> new SystemConfig(configName));
            sc.setConfigBooleanValue(false);
            systemConfigService.save(sc);
            CommandToRun ctr = new CommandToRun();
			ctr.setAdminCommandType(AdminCommandType.RELOADCONFIGS);
			kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
            return success("Blockchain health check has been disabled.");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return error(e.getMessage());
        }
	}

}
