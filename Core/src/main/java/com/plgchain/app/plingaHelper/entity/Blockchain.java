/**
 *
 */
package com.plgchain.app.plingaHelper.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.constant.BlockchainTechType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "\"tblBlockchain\"", schema = "\"schSevice\"")
public class Blockchain implements Serializable {

	private static final long serialVersionUID = -2860375325245572854L;

	@Id
	@SequenceGenerator(name = "TBLBLOCKCHAIN_BLOCKCHAINID_GENERATOR", sequenceName = "\"seqBlockchainBlockchainId\"", schema = "\"schSevice\"", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBLBLOCKCHAIN_BLOCKCHAINID_GENERATOR")
	@Column(name = "\"blockchainId\"")
	private long blockchainId;

	@NotBlank(message = "Blockchain name may not be blank")
	@Column(name = "\"name\"",nullable = false,unique = true)
	private String name;

	@NotBlank(message = "Main coin may not be blank")
	@Column(name = "\"mainCoin\"",nullable = false)
	private String mainCoin;

	@NotBlank(message = "Blockexplorer may not be blank")
	@Column(name = "\"blockExplorer\"",nullable = false)
	private String blockExplorer;

	@Column(name = "\"height\"",nullable = false)
	private BigInteger height;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "\"tech\"",nullable = false)
	private BlockchainTechType blockchainType;

	@Column(name = "\"blockDuration\"",nullable = false)
	private int blockDuration;

	@Column(name = "\"healthy\"",nullable = false)
	private boolean healthy;

	@Column(name = "\"nodeCount\"",nullable = false)
	private int nodeCount;

	@Column(name = "\"enabled\"",nullable = false)
	private boolean enabled;

	@CreationTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"creationDate\"", nullable = false)
	private LocalDateTime creationDate;

	@UpdateTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"lastUpdateDate\"")
	private LocalDateTime lastUpdateDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	@Column(name = "\"lastCheck\"")
	private LocalDateTime lastCheck;

}
