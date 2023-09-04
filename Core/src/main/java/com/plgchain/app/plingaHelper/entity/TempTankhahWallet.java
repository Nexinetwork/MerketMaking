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
import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.dto.EvmWalletDto;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "\"tblTempTankhahWallet\"", schema = "\"schMarketMaking\"")
public class TempTankhahWallet implements Serializable {

	private static final long serialVersionUID = -857239749717471311L;

	@Id
	@SequenceGenerator(name = "TBLTMPTANKHAHWALLET_TMPTANKHAHWALLETID_GENERATOR", sequenceName = "\"seqTempTankhahTempTankhahId\"", schema = "\"schMarketMaking\"", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBLTMPTANKHAHWALLET_TMPTANKHAHWALLETID_GENERATOR")
	@Column(name = "\"tempTankhahId\"",nullable = false)
	private long tempTankhahId;

	@ManyToOne
	@JoinColumn(name = "\"smartContractId\"",nullable = false)
	private SmartContract smartContract;

	@Column(name = "\"privateKey\"",nullable = false)
	private String privateKey;

	@Column(name = "\"publicKey\"",nullable = false)
	private String publicKey;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "\"walletType\"",nullable = false)
	private WalletType walletType;

	private BigInteger nonce;

	@CreationTimestamp
	@Column(name = "\"creationDate\"",nullable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime creationDate;

	@UpdateTimestamp
	@Column(name = "\"lastUpdateDate\"")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime lastUpdateDate;

	public TempTankhahWallet(EvmWalletDto evmw) {
		this.nonce = evmw.getNonce();
		if (this.nonce == null)
			this.nonce = BigInteger.ZERO;
		this.privateKey = evmw.getHexKey();
		this.publicKey = evmw.getPublicKey();
	}

	public EvmWalletDto getAsEvmWalletDto() {
		return EvmWalletDto.builder().hexKey(privateKey).privateKey(privateKey).publicKey(publicKey).build();
	}



}
