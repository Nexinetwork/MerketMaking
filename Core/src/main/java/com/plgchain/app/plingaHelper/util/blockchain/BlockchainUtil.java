package com.plgchain.app.plingaHelper.util.blockchain;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

public class BlockchainUtil implements Serializable {

    private static final long serialVersionUID = -7284069974541384059L;

    public static BigInteger getLatestBlockNumber(HttpClient httpClient,String rpcUrl) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(rpcUrl))
                    .POST(HttpRequest.BodyPublishers.ofString("{\"jsonrpc\":\"2.0\",\"method\":\"eth_blockNumber\",\"params\":[],\"id\":1}"))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                String blockNumberHex = JSON.parseObject(responseBody).getString("result");

                // Parse the JSON response and extract the block number
                // Assuming the response is in the format {"result":"0x123..."}
                return new BigInteger(String.valueOf(EVMUtil.hexToDecimal(blockNumberHex)));
            } else {
                System.err.println("Error: " + response.statusCode() + " " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
