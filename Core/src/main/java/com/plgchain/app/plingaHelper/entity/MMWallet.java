package com.plgchain.app.plingaHelper.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.dto.MarketMakingWalletDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "marketmaking_wallet")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MMWallet {

    @Id
    private long marketMakingId;

    @Indexed(unique = true)
    private long contractId;

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

    public MMWallet(long marketMakingId,long contractId, long coinId, long blockchainId, String blockchain, String coin,
            String contractAddress) {
    	this.marketMakingId = marketMakingId;
        this.contractId = contractId;
        this.coinId = coinId;
        this.blockchainId = blockchainId;
        this.blockchain = blockchain;
        this.coin = coin;
        this.contractAddress = contractAddress;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof MMWallet))
			return false;
		MMWallet other = (MMWallet) obj;
		return (contractId == other.contractId || marketMakingId == other.marketMakingId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(contractId, marketMakingId);
	}


}
