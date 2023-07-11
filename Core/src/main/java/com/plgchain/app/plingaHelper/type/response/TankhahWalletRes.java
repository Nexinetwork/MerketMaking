/**
 *
 */
package com.plgchain.app.plingaHelper.type.response;

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
public class TankhahWalletRes implements Serializable {

	private static final long serialVersionUID = -2160972286619027285L;
	private long tankhahWalletId;

	private String privateKey;

	private String tankhahWalletType;

	private String publicKey;

	private BigDecimal balance;

	private long contractId;

	private String contractAddress;

	private long blockchainId;

	private String blockchain;

	private long coinId;

	private String coinName;

	private String coinSymbol;

}
