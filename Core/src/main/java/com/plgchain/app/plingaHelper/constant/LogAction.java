/**
 *
 */
package com.plgchain.app.plingaHelper.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.plgchain.app.plingaHelper.core.BaseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author eae966
 *
 */
@AllArgsConstructor
@Getter
public enum LogAction implements BaseEnum {
	CREATEBLOCKCHAIN("CREATEBLOCKCHAIN"), CRATENODE("CRATENODE"), SYSTEMACTION("SYSTEMACTION");

	@Setter
    private String perName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return ordinal();
    }
}
