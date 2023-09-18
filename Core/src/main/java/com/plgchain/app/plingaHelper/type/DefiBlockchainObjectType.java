/**
 *
 */
package com.plgchain.app.plingaHelper.type;

import java.io.Serializable;
import java.util.List;

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
public class DefiBlockchainObjectType implements Serializable {

	private static final long serialVersionUID = -1586534522233134104L;

	private long blockchainId;
	private String blockchain;

	private List<DefiContract> contractList;

}
