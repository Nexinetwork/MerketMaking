/**
 *
 */
package com.plgchain.app.plingaHelper.type;

import java.io.Serializable;
import java.util.Objects;

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
public class DefiContract implements Serializable {

	private static final long serialVersionUID = -5180617529389031529L;
	private long contractId;
	private String contractAddress;
	private boolean defiMajorContract;
	private boolean defiStableCoin;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DefiContract)) {
			return false;
		}
		DefiContract other = (DefiContract) obj;
		return Objects.equals(contractAddress, other.contractAddress) || contractId == other.contractId;
	}



}
