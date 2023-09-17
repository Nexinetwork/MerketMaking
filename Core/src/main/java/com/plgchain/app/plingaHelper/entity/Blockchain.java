/**
 *
 */
package com.plgchain.app.plingaHelper.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.constant.BlockchainTechType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
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
@Data
@ToString(exclude = {"nodeList"})
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

	@Column(name = "\"mainCoin\"")
	private String mainCoin;

	@Column(name = "\"blockExplorer\"")
	private String blockExplorer;

	@Column(name = "\"height\"")
	private BigInteger height;

	@Column(name = "\"chainId\"")
	private BigInteger chainId;

	@Column(name = "\"coingeckoId\"",unique = true)
	private String coingeckoId;

	@Column(name = "\"defiV1Factory\"")
	private String defiV1Factory;

	@Column(name = "\"defiV1Router01\"")
	private String defiV1Router01;

	@Column(name = "\"defiV1Router\"")
	private String defiV1Router;

	@Column(name = "\"rpcUrl\"")
	private String rpcUrl;

	@Column(name = "\"fullName\"")
	private String fullName;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "\"tech\"",nullable = false)
	private BlockchainTechType blockchainType;

	@Column(name = "\"blockDuration\"")
	private int blockDuration;

	@Column(name = "\"healthy\"")
	private boolean healthy;

	@Column(name = "\"mustCheck\"",nullable = false)
	private boolean mustCheck;

	@Column(name = "\"isEvm\"",nullable = false)
	private boolean isEvm;

	@Column(name = "\"autoGas\"",nullable = false)
	private boolean autoGas;

	@Column(name = "\"nodeCount\"")
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

	@OneToMany(mappedBy = "blockchain",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JSONField(serialize = false)
	private List<BlockchainNode> nodeList;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Blockchain))
			return false;
		Blockchain other = (Blockchain) obj;
		return blockchainId == other.blockchainId || (Objects.equals(chainId, other.chainId) && isEvm && other.isEvm)
				|| Objects.equals(name, other.name);
	}


}
