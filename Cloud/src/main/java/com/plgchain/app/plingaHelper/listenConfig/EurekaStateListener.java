package com.plgchain.app.plingaHelper.listenConfig;

import java.util.HashMap;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.eureka.server.event.*;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.netflix.appinfo.InstanceInfo;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;

@Component
public class EurekaStateListener {

	private final static Logger logger = LoggerFactory.getLogger(EurekaStateListener.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;
    @Value("${spark.system.host}")
    private String host;
    @Value("${spark.system.name}")
    private String company;

    @Value("${spark.system.admins}")
    private String admins;

    @Value("${spark.system.admin-phones}")
    private String adminPhones;

	@EventListener(condition = "#event.replication==false")
    public void listen(EurekaInstanceCanceledEvent event) {
        String msg="Your service "+event.getAppName()+"\n"+event.getServerId()+"Offline";
        logger.info(msg);

        String[] adminList = admins.split(",");
		for(int i = 0; i < adminList.length; i++) {
			sendEmailMsg(adminList[i], msg, "[Service] Service offline notification");
		}
    }

    @EventListener(condition = "#event.replication==false")
    public void listen(EurekaInstanceRegisteredEvent event) {
        InstanceInfo instanceInfo = event.getInstanceInfo();
        String msg="service"+instanceInfo.getAppName()+"\n"+  instanceInfo.getHostName()+":"+ instanceInfo.getPort()+ " \nip: " +instanceInfo.getIPAddr() +"Register";
        logger.info(msg);

        String[] adminList = admins.split(",");
		for(int i = 0; i < adminList.length; i++) {
			sendEmailMsg(adminList[i], msg, "[Service] Notice of service launch");
		}
    }

    @EventListener
    public void listen(EurekaInstanceRenewedEvent event) {
        logger.info("Service{}renew", event.getServerId() +"  "+ event.getAppName());
    }

    @EventListener
    public void listen(EurekaRegistryAvailableEvent event) {
        logger.info("Registration center started,{}", System.currentTimeMillis());
    }

    @EventListener
    public void listen(EurekaServerStartedEvent event) {
        logger.info("The registration center server starts,{}", System.currentTimeMillis());
    }

    @Async
    public void sendEmailMsg(String email, String msg, String subject) {
    	try {
	        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = null;
	        helper = new MimeMessageHelper(mimeMessage, true);
	        helper.setFrom(from);
	        helper.setTo(email);
	        helper.setSubject(company + "-" + subject);
	        Map<String, Object> model = new HashMap<>(16);
	        model.put("msg", msg);
	        Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
	        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
	        Template template = cfg.getTemplate("simpleMessage.ftl");
	        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
	        helper.setText(html, true);

	        //send email
	        javaMailSender.send(mimeMessage);
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}