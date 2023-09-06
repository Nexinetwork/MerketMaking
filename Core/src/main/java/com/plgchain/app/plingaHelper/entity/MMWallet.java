package com.plgchain.app.plingaHelper.entity;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.dto.MarketMakingWalletDto;

import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "marketmaking_wallet")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "marketmaking_wallet_idx1", def = "{'marketMakingId' : 1, 'chunk' : 1}"),
    @CompoundIndex(name = "marketmaking_wallet_idx2", def = "{'marketMakingId' : 1, 'chunk' : -1}"),
    @CompoundIndex(name = "marketmaking_wallet_idx3", def = "{'contractId' : 1, 'chunk' : 1}"),
    @CompoundIndex(name = "marketmaking_wallet_idx4", def = "{'contractId' : 1, 'chunk' : -1}")
})
public class MMWallet {

	@Transient
    public static final String SEQUENCE_NAME = "mmwallet_sequence";

	@Id
	private long id;

	@Indexed
	private long marketMakingId;

	@Indexed
	private long contractId;

	private int chunk;

	private long coinId;

	private long blockchainId;

	private String blockchain;

	private String coin;

	private String coinSymbol;

	private String contractAddress;

	private List<MarketMakingWalletDto> transferWalletList;
	private List<MarketMakingWalletDto> defiWalletList;

	private List<MarketMakingWalletDto> tankhahTransferWalletList;

    private List<MarketMakingWalletDto> tankhahDefiWalletList;

	@CreatedDate
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime creationDate;

	@LastModifiedDate
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime lastUpdateDate;

	public MMWallet(long marketMakingId, long contractId, int chunk,long coinId, long blockchainId, String blockchain,
			String coin, String contractAddress) {
		this.marketMakingId = marketMakingId;
		this.contractId = contractId;
		this.chunk = chunk;
		this.coinId = coinId;
		this.blockchainId = blockchainId;
		this.blockchain = blockchain;
		this.coin = coin;
		this.contractAddress = contractAddress;
	}

	public MMWallet(MMWallet other) {
		this.marketMakingId = other.getMarketMakingId();
		this.contractId = other.getContractId();
		this.chunk = other.getChunk();
		this.coinId = other.getCoinId();
		this.blockchainId = other.getBlockchainId();
		this.blockchain = other.getBlockchain();
		this.coin = other.getCoin();
		this.contractAddress = other.getContractAddress();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof MMWallet))
			return false;
		MMWallet other = (MMWallet) obj;
		return ((contractId == other.contractId && chunk == other.chunk) || (marketMakingId == other.marketMakingId && chunk == other.chunk));
	}
}
