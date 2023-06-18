/**
 *
 */
package com.plgchain.app.plingaHelper.coingecko.type;

import java.io.Serializable;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

/**
 *
 */
public class CoingeckoUtil implements Serializable {

	private static final long serialVersionUID = 175672548050317226L;

	public static String runGetCommand(String url) {
		while (true) {
			try {
				HttpResponse<String> response = Unirest.get(url).asString();
				return response.getBody();
			} catch (Exception e) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

}
