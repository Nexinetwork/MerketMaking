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
import com.plgchain.app.plingaHelper.entity.Blockchain;

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
@Table(name = "\"tblSmartContract\"", schema = "\"schCoingecko\"")
public class SmartContract implements Serializable {

	private static final long serialVersionUID = 6245353003502803905L;

	@Id
	@SequenceGenerator(name = "TBLSMARTCONTRACT_SMARTCONTRACTID_GENERATOR", sequenceName = "\"seqSmartContractContractId\"", schema = "\"schCoingecko\"", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBLSMARTCONTRACT_SMARTCONTRACTID_GENERATOR")
	@Column(name = "\"contractId\"")
	private long contractId;

	@ManyToOne
	@JoinColumn(name = "\"blockchainId\"")
	private Blockchain blockchain;

	@ManyToOne
	@JoinColumn(name = "\"coinId\"")
	private Coin coin;

	@Column(name = "\"contractsAddress\"",nullable = false)
	private String contractsAddress;

	@Column(name = "\"isMain\"",nullable = false)
	private boolean isMain;

	@Column(name = "\"mustCheck\"",nullable = false)
	private boolean mustCheck;

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



}
