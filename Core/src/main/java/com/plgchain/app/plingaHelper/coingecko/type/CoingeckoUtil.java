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

	public static String runGetCommand(String url) throws Exception {
	    int maxRetries = 10;
	    int retryCount = 0;

	    while (retryCount < maxRetries) {
	        try {
	            HttpResponse<String> response = Unirest.get(url).asString();
	            if (!response.getBody().contains("exceeded the Rate Limit")) {
	                return response.getBody();
	            }
	        } catch (Exception e) {
	            logger.error("Coingecko error: " + e.getMessage());
	        }

	        retryCount++;

	        try {
	            Thread.sleep(5000);
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	        }
	    }

	    throw new Exception("Exceeded maximum retry count");
	}


}
