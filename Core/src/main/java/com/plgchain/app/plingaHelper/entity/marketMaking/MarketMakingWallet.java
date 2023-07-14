/**
 *
 */
package com.plgchain.app.plingaHelper.entity.marketMaking;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.entity.Blockchain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.type.response.MarketMakingWalletRes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "\"tblMMWallet\"", schema = "\"schMarketMaking\"")
public class MarketMakingWallet implements Serializable {

	private static final long serialVersionUID = 2725484378840893771L;

	@Id
	@SequenceGenerator(name = "TBLMMWALLET_MMWALLETID_GENERATOR", sequenceName = "\"seqMMWalletMMWalletId\"", schema = "\"schMarketMaking\"", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBLMMWALLET_MMWALLETID_GENERATOR")
	@Column(name = "\"mmWalletId\"")
	private long mmWalletId;

	@ColumnTransformer(
            read = "PGP_SYM_DECRYPT(\"privateKey\", '!@MYLoveTeted2023secretLOGINILoveYouTedTed@!')",
            write = "PGP_SYM_ENCRYPT (?, '!@MYLoveTeted2023secretLOGINILoveYouTedTed@!')"
    )
	@Column(name = "\"privateKey\"", columnDefinition = "bytea")
	private String privateKey;

	@ColumnTransformer(
            read = "PGP_SYM_DECRYPT(\"privateKeyHex\", '!@MYLoveTeted2023secretLOGINILoveYouTedTed@!')",
            write = "PGP_SYM_ENCRYPT (?, '!@MYLoveTeted2023secretLOGINILoveYouTedTed@!')"
    )
	@Column(name = "\"privateKeyHex\"", columnDefinition = "bytea")
	private String privateKeyHex;

	@Column(name = "\"publicKey\"")
	private String publicKey;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "\"walletType\"")
	private WalletType walletType;

	private BigDecimal balance;

	@ManyToOne
	@JoinColumn(name = "\"contractId\"")
	private SmartContract contract;

	@ManyToOne
	@JoinColumn(name = "\"blockchainId\"")
	private Blockchain blockchain;

	@ManyToOne
	@JoinColumn(name = "\"coinId\"")
	private Coin coin;

	@Column(name = "\"contractAddress\"")
	private String contractAddress;

	@CreationTimestamp
	@Column(name = "\"creationDate\"")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime creationDate;

	@UpdateTimestamp
	@Column(name = "\"lastModifiedDate\"")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime lastModifiedDate;

	public MarketMakingWalletRes getAsMarketMakingWalletRes() {
		return MarketMakingWalletRes.builder().balance(balance).blockchain(blockchain.getName()).blockchainId(blockchain.getBlockchainId())
				.coin(coin.getName()).coinId(coin.getCoinId()).symbol(coin.getSymbol()).contractId(contract.getContractId())
				.smartContract(contract.getContractsAddress()).walletType(walletType).build();
	}

}
