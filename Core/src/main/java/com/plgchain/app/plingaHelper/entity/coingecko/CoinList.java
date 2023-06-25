/**
 *
 */
package com.plgchain.app.plingaHelper.entity.coingecko;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.dto.CoinListDto;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import org.springframework.data.annotation.Id;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 *
 */
@Document(collection = "coingecko_coinlist")
@Data
@ToString
@Builder
public class CoinList implements Serializable {

	private static final long serialVersionUID = -2187069251071859823L;

	@Transient
    public static final String SEQUENCE_NAME = "coinList_sequence";

	@Id
	private long id;

	private String currenOriginaltCoinList;

	private String currentCoinList;

	private String currenOriginaltCoinListWithPlatform;

	private String currentCoinListWithPlatform;;

	@Column(updatable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@CreatedDate
	private LocalDateTime creationDate;

	@LastModifiedDate
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime lastUpdateDate;

	@LastModifiedDate
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime lastCheck;

	public CoinListDto getAsDto() {
		return CoinListDto.builder().id(id).currenOriginaltCoinList(currenOriginaltCoinList)
				.currenOriginaltCoinListWithPlatform(currenOriginaltCoinListWithPlatform)
				.currentCoinList(currentCoinList).currentCoinListWithPlatform(currentCoinListWithPlatform)
				.creationDate(creationDate).lastUpdateDate(lastUpdateDate).lastCheck(lastCheck).build();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CoinList))
			return false;
		CoinList other = (CoinList) obj;
		return Objects.equals(currenOriginaltCoinList, other.currenOriginaltCoinList)
				&& Objects.equals(currenOriginaltCoinListWithPlatform, other.currenOriginaltCoinListWithPlatform)
				&& Objects.equals(currentCoinList, other.currentCoinList)
				&& Objects.equals(currentCoinListWithPlatform, other.currentCoinListWithPlatform) || (id == other.id && id > 0 && other.id > 0);
	}

	@Override
	public int hashCode() {
		return Objects.hash(currenOriginaltCoinList, currenOriginaltCoinListWithPlatform, currentCoinList,
				currentCoinListWithPlatform, id);
	}



}
