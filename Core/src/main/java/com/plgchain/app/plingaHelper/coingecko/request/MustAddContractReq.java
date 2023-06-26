/**
 *
 */
package com.plgchain.app.plingaHelper.coingecko.request;

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
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MustAddContractReq implements Serializable {

	private static final long serialVersionUID = 1468093597351134016L;

	private String blockchain;

	private String coin;

	private String contractAddress;

	private Integer decimal;


}
