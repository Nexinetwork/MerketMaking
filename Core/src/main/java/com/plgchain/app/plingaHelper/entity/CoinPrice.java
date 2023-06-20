/**
 *
 */
package com.plgchain.app.plingaHelper.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.entity.coingecko.Currency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@ToString
@Data
@Table(name = "\"tblCoinPrice\"", schema = "\"schCoingecko\"")
public class CoinPrice implements Serializable {

	private static final long serialVersionUID = -5680240281640861401L;

	@Id
	@SequenceGenerator(name = "TBLCOINPRICE_COINPRICEID_GENERATOR", sequenceName = "\"seqCoinPriceConinPriceId\"", schema = "\"schCoingecko\"", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBLCOINPRICE_COINPRICEID_GENERATOR")
	@Column(name = "\"contractId\"")
	private long coinPriceId;

	@ManyToOne
	@JoinColumn(name = "\"coinId\"")
	private Coin coin;

	@ManyToOne
	@JoinColumn(name = "\"currencyId\"")
	private Currency currency;

	private BigDecimal amount;

	@CreationTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"creationDate\"", nullable = false)
	private LocalDateTime creationDate;

	@UpdateTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"lastUpdateDate\"")
	private LocalDateTime lastUpdateDate;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CoinPrice))
			return false;
		CoinPrice other = (CoinPrice) obj;
		return (Objects.equals(coin, other.coin) && Objects.equals(currency, other.currency)) || coinPriceId == other.coinPriceId
				;
	}


}
