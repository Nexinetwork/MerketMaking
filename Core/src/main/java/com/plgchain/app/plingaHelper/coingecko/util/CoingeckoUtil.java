package com.plgchain.app.plingaHelper.coingecko.util;

import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoingeckoUtil implements Serializable {

    private static final long serialVersionUID = 175672548050317226L;
    private final static Logger logger = LoggerFactory.getLogger(CoingeckoUtil.class);

    public static String runGetCommand(String url) throws Exception {
        int maxRetries = 10;
        int retryCount = 0;

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        while (retryCount < maxRetries) {
            HttpResponse<String> response = null;
            try {
            	String encodedUrl = url.replace("[", "%5B").replace("]", "%5D");

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(encodedUrl))
                        .build();

                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (!response.body().contains("exceeded the Rate Limit")) {
                    return response.body();
                }
            } catch (Exception e) {
                if (e.getMessage().contains("Illegal character")) {
                    logger.info("Invalid character in URL " + url);
                }
                logger.error("Coingecko error: " + e.getMessage());
            } finally {
                if (response != null) {
                    response.body();
                }
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