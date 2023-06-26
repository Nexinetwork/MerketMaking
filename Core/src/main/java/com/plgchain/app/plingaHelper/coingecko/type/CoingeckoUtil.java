/**
 *
 */
package com.plgchain.app.plingaHelper.coingecko.type;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

/**
 *
 */
public class CoingeckoUtil implements Serializable {

	private static final long serialVersionUID = 175672548050317226L;
	private final static Logger logger = LoggerFactory.getLogger(CoingeckoUtil.class);

	public static String runGetCommand(String url) {
		while (true) {
			try {
				HttpResponse<String> response = Unirest.get(url).asString();
				if (!response.getBody().contains("exceeded the Rate Limit"))
					return response.getBody();
			} catch (Exception e) {
				try {
					Thread.sleep(5000);
					logger.error("Coingecko error : " + e.getMessage());
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

}
