/**
 *
 */
package com.plgchain.app.plingaHelper.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMakingWallet;

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

	private String blockchain;

	private String coin;

	private String coinSymbol;

	private String contractAddress;

	private LocalDateTime creationDate;

	private LocalDateTime lastModifiedDate;

	private String encryptedPrivateKey;

	public MarketMakingWalletDto(MarketMakingWallet wallet) {
		// populate the fields of the DTO based on the fields of the MarketMakingWallet
		// entity
		this.mmWalletId = wallet.getMmWalletId();
		this.privateKey = wallet.getPrivateKey();
		this.privateKeyHex = wallet.getPrivateKeyHex();
		this.publicKey = wallet.getPublicKey();
		this.balance = wallet.getBalance();
		this.blockchainId = wallet.getBlockchainId();
		this.blockchain = wallet.getBlockchain().getName();
		this.coin = wallet.getCoin().getName();
		this.coinSymbol = wallet.getCoin().getSymbol();
		this.coinId = wallet.getCoinId();
		this.contractId = wallet.getContractId();
		this.contractAddress = wallet.getContractAddress();
		this.creationDate = wallet.getCreationDate();
		this.lastModifiedDate = wallet.getLastModifiedDate();
		this.mainCoinBalance = wallet.getMainCoinBalance();
		this.walletType = wallet.getWalletType();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof MarketMakingWalletDto))
			return false;
		MarketMakingWalletDto other = (MarketMakingWalletDto) obj;
		return mmWalletId == other.mmWalletId
				|| (Objects.equals(encryptedPrivateKey, other.encryptedPrivateKey) && contractId == other.contractId
						&& walletType == other.walletType)
				|| (contractId == other.contractId && Objects.equals(privateKey, other.privateKey)
						&& walletType == other.walletType)
				|| (contractId == other.contractId && Objects.equals(privateKeyHex, other.privateKeyHex)
						&& walletType == other.walletType)
				|| (contractId == other.contractId && Objects.equals(publicKey, other.publicKey)
						&& walletType == other.walletType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(contractId, encryptedPrivateKey, mmWalletId, privateKey, privateKeyHex, publicKey,
				walletType);
	}

}
