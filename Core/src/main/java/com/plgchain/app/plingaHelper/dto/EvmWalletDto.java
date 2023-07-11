/**
 *
 */
package com.plgchain.app.plingaHelper.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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

}
