/**
 *
 */
package com.plgchain.app.plingaHelper.entity.marketMaking;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@ToString(exclude = {"smartContract"})
@Data
@Table(name = "\"tblMarketMaking\"", schema = "\"schCoingecko\"")
public class MarketMaking implements Serializable {

	private static final long serialVersionUID = 3111778613600984596L;

	@Id
	@SequenceGenerator(name = "TBLMARKETMAKING_MARKETMAKINGID_GENERATOR", sequenceName = "\"seqMarketMakingMarketMakingId\"", schema = "\"schCoingecko\"", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBLMARKETMAKING_MARKETMAKINGID_GENERATOR")
	@Column(name = "\"marketMakingId\"",nullable = false,unique = true)
	private long marketMakingId;

	@OneToOne
	@JoinColumn(name = "\"contractId\"",nullable = false)
	private SmartContract smartContract;

	@Column(name = "\"initialWallet\"",nullable = false)
	private int initialWallet;

	@Column(name = "\"minInitial\"",nullable = false)
	private BigDecimal minInitial;

	@Column(name = "\"maxInitial\"",nullable = false)
	private BigDecimal maxInitial;

	@Column(name = "\"initialDecimal\"",nullable = false)
	private int initialDecimal;

	@Column(name = "\"dailyAddWallet\"",nullable = false)
	private int dailyAddWallet;

	@Column(name = "\"currentTransferWalletCount\"",nullable = false)
	private long currentTransferWalletCount;

	@Column(name = "\"initialWalletCreationDone\"",nullable = false)
	private boolean initialWalletCreationDone;

	@Column(name = "\"initialWalletFundingDone\"",nullable = false)
	private boolean initialWalletFundingDone;

	@CreationTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"creationDate\"", nullable = false)
	private LocalDateTime creationDate;

	@UpdateTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"lastUpdateDate\"")
	private LocalDateTime lastUpdateDate;

}
