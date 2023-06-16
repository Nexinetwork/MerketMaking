/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class ContextRefreshedEvent implements Serializable {

	private static final long serialVersionUID = -3146177381576324841L;

	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {

	}

}
