/**
 *
 */
package com.plgchain.app.plingaHelper.entity.coingecko;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 *
 */
@Document(collection = "coingecko_coin_history")
@Data
@ToString
@Builder
@AllArgsConstructor
public class CoingeckoCoinHistory implements Serializable {

	private static final long serialVersionUID = 5574984318762333767L;

	@Id
	private String id;

	private String symbol;

	private String name;

	private List<CoingeckoCoin> coingeckoCoinHistory;

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

	public CoingeckoCoinHistory() {
        this.coingeckoCoinHistory = new ArrayList<>();
    }

    public CoingeckoCoinHistory(String id, String symbol, String name) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.coingeckoCoinHistory = new ArrayList<>();
    }

    public void addCoingeckoCoin(CoingeckoCoin coingeckoCoin) {
        this.coingeckoCoinHistory.add(coingeckoCoin);
    }

}
