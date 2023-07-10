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
public class SmartContractReq implements Serializable {

	private static final long serialVersionUID = -1627797136882929971L;

	private long contractId;

	private String blockchain;

	private long blockchainId;

	private String coin;

	private long coinId;

	private String contractsAddress;

	private boolean isMain;

	private boolean mustCheck;

	private boolean marketMaking;

	private boolean mustAdd;

	private Integer decimal;

}
