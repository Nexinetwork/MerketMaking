/**
 *
 */
package com.plgchain.app.plingaHelper.entity.coingecko;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "\"tblCurrency\"", schema = "\"schCoingecko\"")
public class Currency implements Serializable {

	private static final long serialVersionUID = -4299737906023339269L;

	@Id
	@NotBlank(message = "currencyId should not be null")
	@Column(name = "\"currencyId\"",nullable = false,unique = true)
	private String currencyId;

	@Column(name = "\"priceInUsd\"")
	private BigDecimal priceInUsd;

	@CreationTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"creationDate\"", nullable = false)
	private LocalDateTime creationDate;

	@UpdateTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"lastUpdateDate\"")
	private LocalDateTime lastUpdateDate;

}
