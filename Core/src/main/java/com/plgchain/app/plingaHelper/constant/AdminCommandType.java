package com.plgchain.app.plingaHelper.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.plgchain.app.plingaHelper.core.BaseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public enum AdminCommandType implements BaseEnum {
	UPDATEBLOCKCHAIN("Update Blockchain"), UPDATECOINS("Update Coins");

	@Setter
	private String englishName;

	@Override
	@JsonValue
	public int getOrdinal() {
		return ordinal();
	}

}
