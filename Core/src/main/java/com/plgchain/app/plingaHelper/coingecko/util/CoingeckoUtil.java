package com.plgchain.app.plingaHelper.coingecko.util;

import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoingeckoUtil implements Serializable {

    private static final long serialVersionUID = 175672548050317226L;
    private final static Logger logger = LoggerFactory.getLogger(CoingeckoUtil.class);

    public static String runGetCommand(HttpClient httpClient,String url) throws Exception {
        int maxRetries = 10;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            HttpResponse<String> response = null;
            String encodedUrl = url.replace("[", "%5B").replace("]", "%5D");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(encodedUrl))
                    .build();
            try {

                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (!response.body().contains("exceeded the Rate Limit")) {
                	String result = response.body();
                	request = null;
                	response = null;
                    return result;
                }
            } catch (Exception e) {
                if (e.getMessage().contains("Illegal character")) {
                    logger.info("Invalid character in URL " + url);
                }
                logger.error("Coingecko error: " + e.getMessage());
            } finally {
                    response = null;
                    request = null;
            }

            retryCount++;
        }

        throw new Exception("Exceeded maximum retry count");
    }
}