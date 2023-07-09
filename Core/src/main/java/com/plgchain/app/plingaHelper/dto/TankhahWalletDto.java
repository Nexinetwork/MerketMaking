/**
 *
 */
package com.plgchain.app.plingaHelper.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;

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
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TankhahWalletDto implements Serializable {

	private static final long serialVersionUID = 1708967235638917863L;

	private long tankhahWalletId;

	private String privateKey;

	private String publicKey;

	private BigDecimal balance;

	private SmartContract contract;

	private long blockchainId;

	private String blockchain;

	private long coinId;

	private String coin;
	
	private String rpcUrl;

}
