/**
 *
 */
package com.plgchain.app.plingaHelper.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.entity.coingecko.CoinList;

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
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CoinListDto implements Serializable {

	private static final long serialVersionUID = 205515297231179702L;

	private long id;


	private String currentOriginalCoinList;

	private String currentCoinList;

	private String currentOriginalCoinListWithPlatform;

	private String currentCoinListWithPlatform;;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime creationDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
    private LocalDateTime lastUpdateDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime lastCheck;

	public CoinList getAsObject() {
		return CoinList.builder().creationDate(creationDate).lastCheck(lastCheck).lastUpdateDate(lastUpdateDate).id(id)
				.currentOriginalCoinList(currentOriginalCoinList).currentOriginalCoinListWithPlatform(currentOriginalCoinListWithPlatform)
				.currentCoinList(currentCoinList).currentCoinListWithPlatform(currentCoinListWithPlatform).build();

	}

}
