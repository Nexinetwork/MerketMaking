/**
 *
 */
package com.plgchain.app.plingaHelper.type.request;

import java.io.Serializable;
import com.plgchain.app.plingaHelper.constant.TankhahWalletType;
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
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TankhahWalletRequest implements Serializable {

	private static final long serialVersionUID = -1738982851516807768L;

	private TankhahWalletType tankhahWalletType;

	private int num;

	private String contract;

	private long contractId;

	private String blockchain;

	private long blockchainId;

	private String coinGeckoId;

	private long coinId;



}
