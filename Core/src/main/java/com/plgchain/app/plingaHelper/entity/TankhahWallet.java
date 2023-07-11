/**
 *
 */
package com.plgchain.app.plingaHelper.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.constant.TankhahWalletType;
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
@Table(name = "\"tblTankhahWallet\"", schema = "\"schMarketMaking\"")
public class TankhahWallet implements Serializable {

	private static final long serialVersionUID = 2725484378840893771L;

	@Id
	@SequenceGenerator(name = "TBLTANKHAHWALLET_TANKHAHWALLETID_GENERATOR", sequenceName = "\"seqTankhahWalletTankhahWalletId\"", schema = "\"schMarketMaking\"", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBLTANKHAHWALLET_TANKHAHWALLETID_GENERATOR")
	@Column(name = "\"tankhahWalletId\"")
	private long tankhahWalletId;

	@ColumnTransformer(
            read = "PGP_SYM_DECRYPT(\"privateKey\", '!@MYLoveTeted2023secretLOGINILoveYouTedTed@!')",
            write = "PGP_SYM_ENCRYPT (?, '!@MYLoveTeted2023secretLOGINILoveYouTedTed@!')"
    )
	@Column(name = "\"privateKey\"", columnDefinition = "bytea")
	private String privateKey;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "\"tankhahWalletType\"")
	private TankhahWalletType tankhahWalletType;

	@Column(name = "\"publicKey\"")
	private String publicKey;

	private BigDecimal balance;

	@ManyToOne
	@JoinColumn(name = "\"contractId\"")
	private SmartContract contract;

	@CreationTimestamp
	@Column(name = "\"creationDate\"")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime creationDate;

	@UpdateTimestamp
	@Column(name = "\"lastModifiedDate\"")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime lastModifiedDate;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof TankhahWallet))
			return false;
		TankhahWallet other = (TankhahWallet) obj;
		return (Objects.equals(contract, other.contract) && Objects.equals(privateKey, other.privateKey)
				 && tankhahWalletType == other.tankhahWalletType) || tankhahWalletId == other.tankhahWalletId;
	}

}
