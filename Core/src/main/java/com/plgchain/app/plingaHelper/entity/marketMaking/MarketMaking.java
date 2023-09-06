/**
 *
 */
package com.plgchain.app.plingaHelper.entity.marketMaking;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.constant.TransactionParallelType;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.type.response.MarketMakingResponse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "smartContract" })
@Data
@Table(name = "\"tblMarketMaking\"", schema = "\"schCoingecko\"")
public class MarketMaking implements Serializable {

	private static final long serialVersionUID = 3111778613600984596L;

	@Id
	@SequenceGenerator(name = "TBLMARKETMAKING_MARKETMAKINGID_GENERATOR", sequenceName = "\"seqMarketMakingMarketMakingId\"", schema = "\"schCoingecko\"", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBLMARKETMAKING_MARKETMAKINGID_GENERATOR")
	@Column(name = "\"marketMakingId\"", nullable = false, unique = true)
	private long marketMakingId;

	@OneToOne
	@JoinColumn(name = "\"contractId\"", nullable = false)
	private SmartContract smartContract;

	@Column(name = "\"initialWallet\"", nullable = false)
	private int initialWallet;

	@Column(name = "\"initialDefiWallet\"", nullable = false)
	private int initialDefiWallet;

	@Column(name = "\"chunkCount\"")
	private Integer chunkCount;

	@Column(name = "\"minInitial\"", nullable = false)
	private BigDecimal minInitial;

	@Column(name = "\"maxInitial\"", nullable = false)
	private BigDecimal maxInitial;

	@Column(name = "\"minDefiInitial\"", nullable = false)
	private BigDecimal minDefiInitial;

	@Column(name = "\"maxDefiInitial\"", nullable = false)
	private BigDecimal maxDefiInitial;

	@Column(name = "\"initialDecimal\"", nullable = false)
	private int initialDecimal;

	@Column(name = "\"dailyAddWallet\"", nullable = false)
	private int dailyAddWallet;

	@Column(name = "\"currentTransferWalletCount\"", nullable = false)
	private long currentTransferWalletCount;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "\"transactionParallelType\"")
	private TransactionParallelType transactionParallelType;

	@Column(name = "\"initialWalletCreationDone\"", nullable = false)
	private boolean initialWalletCreationDone;

	@Column(name = "\"initialWalletFundingDone\"", nullable = false)
	private boolean initialWalletFundingDone;

	@Column(name = "\"initialDefiWalletCreationDone\"", nullable = false)
	private boolean initialDefiWalletCreationDone;

	@Column(name = "\"initialDefiWalletFundingDone\"", nullable = false)
	private boolean initialDefiWalletFundingDone;

	@Column(name = "\"mustUpdateMongoTransfer\"", nullable = false)
	private boolean mustUpdateMongoTransfer;

	@Column(name = "\"mustUpdateMongoDefi\"", nullable = false)
	private boolean mustUpdateMongoDefi;

	@Column(name = "\"trPid\"")
	private String trPid;

	@Column(name = "\"dfPid\"")
	private String dfPid;

	@CreationTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"creationDate\"", nullable = false)
	private LocalDateTime creationDate;

	@UpdateTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"lastUpdateDate\"")
	private LocalDateTime lastUpdateDate;

	public MarketMakingResponse getAsMarketMakingResponse() {
		return Optional.ofNullable(smartContract).map(sc -> MarketMakingResponse.builder().trPid(trPid).dfPid(dfPid)
				.transactionParallelType(transactionParallelType).mustUpdateMongoTransfer(mustUpdateMongoTransfer)
				.creationDate(creationDate).dailyAddWallet(dailyAddWallet)
				.currentTransferWalletCount(currentTransferWalletCount).lastUpdateDate(lastUpdateDate)
				.initialDefiWalletFundingDone(initialDefiWalletFundingDone).initialWallet(initialWallet)
				.initialWalletCreationDone(initialWalletCreationDone).initialWalletFundingDone(initialWalletFundingDone)
				.marketMakingId(marketMakingId).initialDefiWalletCreationDone(initialDefiWalletCreationDone)
				.minDefiInitial(minDefiInitial).minInitial(minInitial).maxInitial(maxInitial)
				.mustUpdateMongoDefi(mustUpdateMongoDefi).mustUpdateMongoTransfer(mustUpdateMongoTransfer)
				.maxDefiInitial(maxDefiInitial).initialDefiWallet(initialDefiWallet).initialDecimal(initialDecimal)
				.contractId(sc.getContractId()).contractsAddress(sc.getContractsAddress())
				.coin(sc.getCoin() != null ? sc.getCoin().getName() : null)
				.coinSymbol(sc.getCoin() != null ? sc.getCoin().getSymbol() : null)
				.coinId(sc.getCoin() != null ? sc.getCoin().getCoinId() : null)
				.coingeckoId(sc.getCoin() != null ? sc.getCoin().getCoingeckoId() : null)
				.blockchainId(sc.getBlockchain() != null ? sc.getBlockchain().getBlockchainId() : null)
				.blockchain(sc.getBlockchain() != null ? sc.getBlockchain().getName() : null).build()).orElse(null);
	}

}
