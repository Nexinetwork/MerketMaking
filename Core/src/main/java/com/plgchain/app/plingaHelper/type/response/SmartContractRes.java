/**
 *
 */
package com.plgchain.app.plingaHelper.type.response;

import java.io.Serializable;
import java.math.BigInteger;

import com.plgchain.app.plingaHelper.constant.BlockchainTechType;

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
public class SmartContractRes implements Serializable {

	private static final long serialVersionUID = 5052396188341600593L;

	private long contractId;

	private long blockchainId;

	private String coinName;

	private String mainCoin;

	private BigInteger chainId;

	private String rpcUrl;

	private String contractsAddress;

	private long coinId;

	private String coinCoingeckoId;

	private String coinSysmbol;

	private BlockchainTechType blockchainType;

	private String blockchainCoingeckoId;

	private Integer decimal;

}
