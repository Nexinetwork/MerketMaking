/**
 *
 */
package com.plgchain.app.plingaHelper.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.plgchain.app.plingaHelper.constant.WalletType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class MarketMakingWalletDto implements Serializable {

	private static final long serialVersionUID = -6868446442548142030L;

	private long mmWalletId;

	private String privateKey;

	private String privateKeyHex;

	private String publicKey;

	@Enumerated(EnumType.ORDINAL)
	private WalletType walletType;

	private BigDecimal balance;

	private BigDecimal mainCoinBalance;

	private long contractId;

	private long coinId;

	private long blockchainId;

	private String contractAddress;

	private LocalDateTime creationDate;

	private LocalDateTime lastModifiedDate;
}
