/**
 *
 */
package com.plgchain.app.plingaHelper.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.entity.coingecko.CoingeckoCoin;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoingeckoCoinAssetDto implements Serializable {

	private static final long serialVersionUID = 1878591421612666304L;

	private String id;

	private String symbol;

	private String name;

	private String mainPlatform;

	private JSONObject platforms;

	private JSONObject detail_platforms;

	private JSONObject market_data;

	private JSONObject current_price;

	private JSONObject market_cap;

	private Integer market_cap_rank;

	private BigDecimal total_supply;

	private BigDecimal max_supply;

	private BigDecimal priceInUsd;

	private JSONObject image;

	private JSONObject total_volume;

	private JSONObject high_24h;

	private BigDecimal high24HInUsd;

	private JSONObject low_24h;

	private BigDecimal low24HInUsd;

	private BigDecimal price_change_24h;

	private BigDecimal price_change_percentage_24h;

	private BigDecimal market_cap_change_24h;

	private BigDecimal market_cap_change_percentage_24h;

	private String mainContractAddress;

	private String originalJson;

	private String editedJson;

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

	public CoingeckoCoinAssetDto(CoingeckoCoin coin) {
	    this.id = coin.getId();
	    this.symbol = coin.getSymbol();
	    this.name = coin.getName();
	    this.mainPlatform = coin.getMainPlatform();
	    this.platforms = coin.getPlatforms();
	    this.detail_platforms = coin.getDetail_platforms();
	    this.market_data = coin.getMarket_data();
	    this.current_price = coin.getCurrent_price();
	    this.market_cap = coin.getMarket_cap();
	    this.market_cap_rank = coin.getMarket_cap_rank();
	    this.total_supply = coin.getTotal_supply();
	    this.max_supply = coin.getMax_supply();
	    this.priceInUsd = coin.getPriceInUsd();
	    this.image = coin.getImage();
	    this.total_volume = coin.getTotal_volume();
	    this.high_24h = coin.getHigh_24h();
	    this.high24HInUsd = coin.getHigh24HInUsd();
	    this.low_24h = coin.getLow_24h();
	    this.low24HInUsd = coin.getLow24HInUsd();
	    this.price_change_24h = coin.getPrice_change_24h();
	    this.price_change_percentage_24h = coin.getPrice_change_percentage_24h();
	    this.market_cap_change_24h = coin.getMarket_cap_change_24h();
	    this.market_cap_change_percentage_24h = coin.getMarket_cap_change_percentage_24h();
	    this.mainContractAddress = coin.getMainContractAddress();
	    this.originalJson = coin.getOriginalJson();
	    this.editedJson = coin.getEditedJson();
	    this.creationDate = coin.getCreationDate();
	    this.lastUpdateDate = coin.getLastUpdateDate();
	    this.lastCheck = coin.getLastCheck();
	}


}
