/**
 *
 */
package com.plgchain.app.plingaHelper.entity.coingecko;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.dto.CoingeckoCoinAssetDto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 */
@Document(collection = "coingecko_coin")
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoingeckoCoin implements Serializable {

	private static final long serialVersionUID = -4988989211140382515L;

	@Id
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

	public CoingeckoCoin(JSONObject jo) {
		this.id = jo.getString("id");
		this.symbol = jo.getString("symbol");
		this.name = jo.getString("symbol");
		this.mainPlatform = jo.getString("asset_platform_id");
		this.platforms = jo.getJSONObject("platforms");
		this.detail_platforms = jo.getJSONObject("detail_platforms");
		this.market_data = jo.getJSONObject("market_data");
		if (jo.getJSONObject("market_data").getJSONObject("current_price") != null)
			this.current_price = jo.getJSONObject("market_data").getJSONObject("current_price");
		if (jo.getJSONObject("market_data").getJSONObject("market_cap") != null)
			this.market_cap = jo.getJSONObject("market_data").getJSONObject("market_cap");
		this.market_cap_rank = jo.getInteger("market_cap_rank");
		if (jo.getJSONObject("market_data").getBigDecimal("total_supply") != null)
			this.total_supply = jo.getJSONObject("market_data").getBigDecimal("total_supply");
		this.max_supply = jo.getJSONObject("market_data").getBigDecimal("max_supply");
		this.priceInUsd = jo.getJSONObject("market_data").getJSONObject("current_price").getBigDecimal("usd");
		this.image = jo.getJSONObject("image");
		this.total_volume = jo.getJSONObject("market_data").getJSONObject("total_volume");
		this.high_24h = jo.getJSONObject("market_data").getJSONObject("high_24h");
		this.high24HInUsd = jo.getJSONObject("market_data").getJSONObject("high_24h").getBigDecimal("usd");
		this.low_24h = jo.getJSONObject("market_data").getJSONObject("low_24h");
		this.low24HInUsd = jo.getJSONObject("market_data").getJSONObject("low_24h").getBigDecimal("usd");
		this.price_change_24h = jo.getJSONObject("market_data").getBigDecimal("price_change_24h");
		this.price_change_percentage_24h = jo.getJSONObject("market_data").getBigDecimal("price_change_percentage_24h");
		this.market_cap_change_24h = jo.getJSONObject("market_data").getBigDecimal("market_cap_change_24h");
		this.market_cap_change_percentage_24h = jo.getJSONObject("market_data")
				.getBigDecimal("market_cap_change_percentage_24h");
		this.mainContractAddress = jo.getString("contract_address");
	}

	public CoingeckoCoin(CoingeckoCoinAssetDto dto) {
		this.id = dto.getId();
		this.symbol = dto.getSymbol();
		this.name = dto.getName();
		this.mainPlatform = dto.getMainPlatform();
		this.platforms = dto.getPlatforms();
		this.detail_platforms = dto.getDetail_platforms();
		this.market_data = dto.getMarket_data();
		this.current_price = dto.getCurrent_price();
		this.market_cap = dto.getMarket_cap();
		this.market_cap_rank = dto.getMarket_cap_rank();
		this.total_supply = dto.getTotal_supply();
		this.max_supply = dto.getMax_supply();
		this.priceInUsd = dto.getPriceInUsd();
		this.image = dto.getImage();
		this.total_volume = dto.getTotal_volume();
		this.high_24h = dto.getHigh_24h();
		this.high24HInUsd = dto.getHigh24HInUsd();
		this.low_24h = dto.getLow_24h();
		this.low24HInUsd = dto.getLow24HInUsd();
		this.price_change_24h = dto.getPrice_change_24h();
		this.price_change_percentage_24h = dto.getPrice_change_percentage_24h();
		this.market_cap_change_24h = dto.getMarket_cap_change_24h();
		this.market_cap_change_percentage_24h = dto.getMarket_cap_change_percentage_24h();
		this.mainContractAddress = dto.getMainContractAddress();
		this.originalJson = dto.getOriginalJson();
		this.editedJson = dto.getEditedJson();
		this.creationDate = dto.getCreationDate();
		this.lastUpdateDate = dto.getLastUpdateDate();
		this.lastCheck = dto.getLastCheck();
	}

	public CoingeckoCoinAssetDto getAsDto() {
		return CoingeckoCoinAssetDto.builder().id(id).symbol(symbol).name(name).mainPlatform(mainPlatform)
				.platforms(platforms).detail_platforms(detail_platforms).market_data(market_data)
				.current_price(current_price).market_cap(market_cap).market_cap_rank(market_cap_rank)
				.total_supply(total_supply).max_supply(max_supply).priceInUsd(priceInUsd).image(image)
				.total_volume(total_volume).high_24h(high_24h).high24HInUsd(high24HInUsd).low_24h(low_24h)
				.low24HInUsd(low24HInUsd).price_change_24h(price_change_24h)
				.price_change_percentage_24h(price_change_percentage_24h).market_cap_change_24h(market_cap_change_24h)
				.market_cap_change_percentage_24h(market_cap_change_percentage_24h)
				.mainContractAddress(mainContractAddress).originalJson(originalJson).editedJson(editedJson)
				.creationDate(creationDate).lastUpdateDate(lastUpdateDate).lastCheck(lastCheck).build();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CoingeckoCoin))
			return false;
		CoingeckoCoin other = (CoingeckoCoin) obj;
		return Objects.equals(editedJson, other.editedJson) && id == other.id
				&& Objects.equals(originalJson, other.originalJson);
	}

}
