/**
 *
 */
package com.plgchain.app.plingaHelper.entity.coingecko;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
@ToString
@Data
@Table(name = "\"tblCoin\"", schema = "\"schCoingecko\"")
public class Coin implements Serializable {

	private static final long serialVersionUID = 7107524177500856197L;

	@Id
	@SequenceGenerator(name = "TBLCOIN_COINID_GENERATOR", sequenceName = "\"seqCoinCoinId\"", schema = "\"schCoingecko\"", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBLCOIN_COINID_GENERATOR")
	@Column(name = "\"coinId\"")
	private long coinId;

	@JSONField(name = "id")
	@Column(name = "\"coingeckoId\"",nullable = false,unique = true)
	private String coingeckoId;

	private String symbol;

	private String name;

	@Column(name = "\"mustCheck\"",nullable = false)
	private boolean mustCheck;

	@Column(name = "\"listed\"",nullable = false)
	private boolean listed;

	@Column(name = "\"priceInUsd\"")
	private BigDecimal priceInUsd;

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

	@Transient
	private JSONObject platforms;

}
