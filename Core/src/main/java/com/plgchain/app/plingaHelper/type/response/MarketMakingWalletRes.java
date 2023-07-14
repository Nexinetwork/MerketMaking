package com.plgchain.app.plingaHelper.type.response;

import java.io.Serializable;
import java.math.BigDecimal;

import com.plgchain.app.plingaHelper.constant.WalletType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MarketMakingWalletRes implements Serializable {

	private static final long serialVersionUID = -5245562431674177791L;

	private long walletId;

	private String privateKey;

	private String privateKeyHex;

	private String publicKey;

	private WalletType walletType;

	private BigDecimal balance;

	private long contractId;

	private String smartContract;

	private long blockchainId;

	private String blockchain;

	private long coinId;

	private String coin;

	private String symbol;



}
