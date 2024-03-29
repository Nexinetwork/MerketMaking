package com.plgchain.app.plingaHelper.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.plgchain.app.plingaHelper.core.BaseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public enum AdminCommandType implements BaseEnum {
	UPDATEBLOCKCHAIN("Update Blockchain"), UPDATECOINS("Update Coins"),
	FIXTRANSFERWALLETFUNDING("Fix Transfer Wallet funding"),
	FIXTRANSFERWALLETFUNDINGREVERSE("Fix Transfer Wallet funding reverse"),
	STOPALLNODESOFBLOCKCHAIN("Stop All Nodes Of Blockchain"),
	STOPALLNODESOFALLBLOCKCHAINS("Stop All Nodes Of ALL Blockchains"),
	STARTALLNODESOFBLOCKCHAIN("Start All Nodes Of Blockchain"),
	STARTALLNODESOFALLBLOCKCHAINS("Start All Nodes Of ALL Blockchains"),
	RESTARTALLNODESOFBLOCKCHAIN("Restart All Nodes Of Blockchain"),
	RESTARTALLNODESOFALLBLOCKCHAINS("Restart All Nodes Of ALL Blockchains"), RELOADCONFIGS("Reload configs"),
	BACKALLTOKENTOTANKHAH("Back all token to tankhah"),
	BACKALLTOKENTOTANKHAHPPARALLEL("Back all token to tankhah parallel"),
	BACKALLTOKENTOTANKHAHREVERSE("Back all token to tankhah reverse"),
	BACKALLFROMTMPTANKHAHTOTANKHAH("Back all from temp tankhah to tankhah"),
	UPDATEALLWALLETSBALANCESBYCONTRACTID("Update All Wallets Balances By ContractId"),
	UPDATEALLWALLETSBALANCESBYCONTRACTIDPARALLEL("Update All Wallets Balances By ContractId Parallel"),
	CREDITMAINCOINBALANCEINTOKENWALLETS("Credit main coin balance in token wallets"),
	CREDITMAINCOINBALANCEINTOKENWALLETSREVERSE("Credit main coin balance in token wallets reverse"),
	UPDATECONTRACTADDRESS("Update Contract Address"),
	FIXPRIVATEKEYBYCONTRACTID("Fix Private Key By ContractId"),
	APPROVECONTRACTFORTOKENCONTRACTID("Approve Contract For Token Contract"),
	FUNDACCOUNTFORCONTRACT("Fund Account for contract"),
	DELETETEMPTANKHAHWALLET("Delete temp tankhah wallet");

	@Setter
	private String englishName;

	@Override
	@JsonValue
	public int getOrdinal() {
		return ordinal();
	}

}
