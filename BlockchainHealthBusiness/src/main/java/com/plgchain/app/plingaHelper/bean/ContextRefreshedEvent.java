/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

/**
 *
 */
@Component
public class ContextRefreshedEvent implements Serializable {

	private static final long serialVersionUID = -3146177381576324841L;

	@Inject
	private InitBean initBean;

	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {

	}

}
