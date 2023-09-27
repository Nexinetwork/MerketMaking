/**
 *
 */
package com.plgchain.app.plingaHelper.config;

import java.io.Serializable;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


/**
 * @author eae966
 *
 */
@Configuration
public class KafkaTopicConfig implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 6508485096877479875L;

	/*
	 * @Bean public NewTopic createNewSmartContract() { return
	 * TopicBuilder.name(SysConstant.KAFKA_SMARTCONTRACT_CREATENEW).build(); }
	 */

}
