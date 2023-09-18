/**
 *
 */
package com.plgchain.app.plingaHelper.entity.coingecko;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.TankhahWallet;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMaking;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMakingWallet;
import com.plgchain.app.plingaHelper.type.response.SmartContractRes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@ToString(exclude = {"tankhahWalletList","mmWalletList"})
@Data
@Table(name = "\"tblSmartContract\"", schema = "\"schCoingecko\"")
public class SmartContract implements Serializable {

	private static final long serialVersionUID = 6245353003502803905L;

	@Id
	@SequenceGenerator(name = "TBLSMARTCONTRACT_SMARTCONTRACTID_GENERATOR", sequenceName = "\"seqSmartContractContractId\"", schema = "\"schCoingecko\"", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBLSMARTCONTRACT_SMARTCONTRACTID_GENERATOR")
	@Column(name = "\"contractId\"")
	private long contractId;

	@ManyToOne
	@JoinColumn(name = "\"blockchainId\"")
	private Blockchain blockchain;

	@ManyToOne
	@JoinColumn(name = "\"coinId\"")
	private Coin coin;

	@OneToOne(mappedBy = "smartContract",fetch = FetchType.EAGER)
	private MarketMaking marketMakingObject;

	@OneToMany(mappedBy = "contract")
	private List<TankhahWallet> tankhahWalletList;

	@OneToMany(mappedBy = "contract")
	private List<MarketMakingWallet> mmWalletList;

	@Column(name = "\"contractsAddress\"",nullable = false)
	private String contractsAddress;

	@Column(name = "\"isMain\"",nullable = false)
	private boolean isMain;

	@Column(name = "\"mustCheck\"",nullable = false)
	private boolean mustCheck;

	@Column(name = "\"marketMaking\"",nullable = false)
	private boolean marketMaking;

	@Column(name = "\"mustAdd\"",nullable = false)
	private boolean mustAdd;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"lastCheck\"")
	private LocalDateTime lastCheck;

	@CreationTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"creationDate\"", nullable = false)
	private LocalDateTime creationDate;

	@UpdateTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"lastUpdateDate\"")
	private LocalDateTime lastUpdateDate;

	private Integer decimal;

	@Column(name = "\"defiMajorContract\"",nullable = false)
	private boolean defiMajorContract;

	@Column(name = "\"defiStableCoin\"",nullable = false)
	private boolean defiStableCoin;

	public SmartContractRes getSmartContractRes() {
		return SmartContractRes.builder().blockchainCoingeckoId(blockchain.getCoingeckoId()).blockchainId(blockchain.getBlockchainId()).blockchainType(blockchain.getBlockchainType())
				.chainId(blockchain.getChainId()).coinCoingeckoId(coin.getCoingeckoId()).coinId(coin.getCoinId()).coinName(coin.getName()).coinSysmbol(coin.getSymbol())
				.contractId(contractId).contractsAddress(contractsAddress).decimal(decimal).mainCoin(blockchain.getMainCoin()).rpcUrl(blockchain.getRpcUrl())
				.build();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SmartContract))
			return false;
		SmartContract other = (SmartContract) obj;
		return (Objects.equals(blockchain, other.blockchain)
				&& Objects.equals(contractsAddress, other.contractsAddress)) || contractId == other.contractId;
	}





}
