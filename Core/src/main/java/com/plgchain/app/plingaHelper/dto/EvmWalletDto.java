/**
 *
 */
package com.plgchain.app.plingaHelper.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class EvmWalletDto implements Serializable {

	private static final long serialVersionUID = -4605889243386087217L;

	private BigInteger bigInt;

	private String hexKey;

	private String publicKey;

	private String privateKey;

	private BigDecimal balance;

	private BigInteger nonce;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof EvmWalletDto))
			return false;
		EvmWalletDto other = (EvmWalletDto) obj;
		return Objects.equals(bigInt, other.bigInt) || Objects.equals(hexKey, other.hexKey)
				|| Objects.equals(privateKey, other.privateKey) || Objects.equals(publicKey, other.publicKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(bigInt, hexKey, privateKey, publicKey);
	}



}
