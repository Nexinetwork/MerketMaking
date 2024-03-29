/**
 *
 */
package com.plgchain.app.plingaHelper.type.request;

import java.io.Serializable;
import java.math.BigDecimal;

import com.plgchain.app.plingaHelper.constant.TransactionParallelType;

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
@ToString
@Data
public class MarketMakingReq implements Serializable {

	private static final long serialVersionUID = 452020347609436109L;

	private long marketMakingId;

	private long smartContractId;

	private int initialWallet;

	private BigDecimal minInitial;

	private BigDecimal maxInitial;

	private int initialDecimal;

	private int dailyAddWallet;

	private int initialDefiWallet;

	private BigDecimal minDefiInitial;

	private BigDecimal maxDefiInitial;

	private long currentTransferWalletCount;

	private boolean initialWalletCreationDone;

	private boolean initialWalletFundingDone;

	private boolean initialDefiWalletFundingDone;

	private boolean initialDefiWalletCreationDone;

	private TransactionParallelType transactionParallelType;

	private boolean mustUpdateMongoTransfer;

	private boolean mustUpdateMongoDefi;

}
