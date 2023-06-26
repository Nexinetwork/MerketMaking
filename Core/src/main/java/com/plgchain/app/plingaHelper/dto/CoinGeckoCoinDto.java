/**
 *
 */
package com.plgchain.app.plingaHelper.dto;

import java.io.Serializable;

import com.alibaba.fastjson2.JSONObject;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 *
 */
@Builder
@ToString
@Data
public class CoinGeckoCoinDto implements Serializable {

	private static final long serialVersionUID = 2071383801800034904L;

	private String id;

	private String symbol;

	private String name;

	private JSONObject platforms;


}
