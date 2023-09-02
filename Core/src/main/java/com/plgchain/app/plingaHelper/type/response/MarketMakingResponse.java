/**
 *
 */
package com.plgchain.app.plingaHelper.type.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plgchain.app.plingaHelper.constant.TransactionParallelType;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.TankhahWallet;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMaking;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMakingWallet;

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
public class MarketMakingResponse implements Serializable {

	private static final long serialVersionUID = 3096691089632074316L;

	private long marketMakingId;

	private String blockchain;

	private long blockchainId;

	private String coin;

	private String coingeckoId;

	private long coinId;

	private String contractsAddress;

	private long contractId;

	private String coinSymbol;

	private boolean isMain;

	private boolean mustCheck;

	private boolean marketMaking;

	private boolean mustAdd;

	private Integer decimal;

	private int initialWallet;

	private int initialDefiWallet;

	private BigDecimal minInitial;

	private BigDecimal maxInitial;

	private BigDecimal minDefiInitial;

	private BigDecimal maxDefiInitial;

	private int initialDecimal;

	private int dailyAddWallet;

	private long currentTransferWalletCount;

	private TransactionParallelType transactionParallelType;

	private boolean initialWalletCreationDone;

	private boolean initialWalletFundingDone;

	private boolean initialDefiWalletCreationDone;

	private boolean initialDefiWalletFundingDone;

	private boolean mustUpdateMongoTransfer;

	private boolean mustUpdateMongoDefi;

	private String trPid;

	private String dfPid;

	private LocalDateTime creationDate;

	private LocalDateTime lastUpdateDate;





}
