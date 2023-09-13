/**
 *
 */
package com.plgchain.app.plingaHelper.type.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.plgchain.app.plingaHelper.constant.TransactionParallelType;

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
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContractResponse implements Serializable {

	private static final long serialVersionUID = 2013342944565663762L;

	private long contractId;

	private String contractsAddress;

	private long blockchainId;

	private String blockchain;

	private long coinId;

	private String coinName;

	private String coinSymbol;

	private long marketMakingId;

	private boolean isMain;

	private boolean mustCheck;

	private boolean marketMaking;

	private boolean mustAdd;

	private LocalDateTime lastCheck;

	private LocalDateTime creationDate;

	private LocalDateTime lastUpdateDate;

	private Integer decimal;



}
