/**
 *
 */
package com.plgchain.app.plingaHelper.coingecko.request;

import java.io.Serializable;

import com.alibaba.fastjson2.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 */
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoinNetwork implements Serializable {

	private static final long serialVersionUID = 7114859092873825554L;

	private String id;
	private String symbol;
	private String name;
	private JSONObject platforms;


}
