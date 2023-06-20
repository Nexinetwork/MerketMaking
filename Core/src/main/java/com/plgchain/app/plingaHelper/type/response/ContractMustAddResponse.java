/**
 *
 */
package com.plgchain.app.plingaHelper.type.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ContractMustAddResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String contract;
	private Integer decimal;
	private String blockchainCoingeckoId;
	private String coinCoingeckoId;

}
