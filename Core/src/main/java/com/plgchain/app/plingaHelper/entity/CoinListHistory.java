/**
 *
 */
package com.plgchain.app.plingaHelper.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.dto.CoinListDto;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.ToString;

/**
 *
 */
@Document(collection = "coingecko_coinlist_history")
@Data
@ToString
public class CoinListHistory implements Serializable {

	private static final long serialVersionUID = 1586593125963464687L;

	private List<CoinListDto> coinListList = new ArrayList<CoinListDto>();

	@Column(updatable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@CreatedDate
	private LocalDateTime creationDate;

	@LastModifiedDate
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime lastUpdateDate;

}
