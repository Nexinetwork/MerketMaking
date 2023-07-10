/**
 *
 */
package com.plgchain.app.plingaHelper.type.request;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CoinReq implements Serializable {

	private static final long serialVersionUID = -4981088675826495780L;

	private String coingeckoId;

	private String symbol;

	private String name;

	private String coingeckoJson;

	private boolean mustCheck;

	private boolean listed;

	private BigDecimal priceInUsd;


}
