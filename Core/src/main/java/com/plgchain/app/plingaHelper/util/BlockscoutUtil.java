/**
 *
 */
package com.plgchain.app.plingaHelper.util;

import java.io.Serializable;
import java.math.BigInteger;

import com.alibaba.fastjson2.JSON;
import com.plgchain.app.plingaHelper.util.blockscout.request.JsonRequest;
import com.plgchain.app.plingaHelper.util.blockscout.response.EthRpcResponse;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

/**
 *
 */
public class BlockscoutUtil implements Serializable {

	private static final long serialVersionUID = 1288641661725553063L;

	public static BigInteger getLatestBlock(String url) {
		url += "/api/eth-rpc";
		Object[] emptyParams = new Object[0];
		JsonRequest request = new JsonRequest(0, "2.0", "eth_blockNumber", emptyParams);
		String jsonString = request.toJsonString();
		System.out.println("Url : " + url);
		System.out.println("jsonString : " + jsonString);
		try {
		HttpResponse<String> response = Unirest.post(url)
				.header("content-type", "application/json")
				.body(jsonString).asString();

		EthRpcResponse res = JSON.parseObject(response.getBody(),EthRpcResponse.class);
		return new BigInteger(res.getResult() .substring(2), 16);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
