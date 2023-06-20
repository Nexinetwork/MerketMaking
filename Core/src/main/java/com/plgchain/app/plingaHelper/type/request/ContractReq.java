/**
 *
 */
package com.plgchain.app.plingaHelper.type.request;

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
public class ContractReq implements Serializable {

	private static final long serialVersionUID = 1L;

	private String contract;

	private boolean mustAdd;

	private Integer decimal;

	private boolean mustCheck;

	private boolean isMain;

	private String blockchainCoingeckoId;

	private String coinCoingeckoId;


}
