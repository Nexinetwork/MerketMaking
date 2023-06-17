/**
 *
 */
package com.plgchain.app.plingaHelper.coingecko.type;

import java.io.Serializable;
import java.math.BigInteger;

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
public class AssetPlatform implements Serializable {

	private static final long serialVersionUID = 8329693041644084447L;

	private String id;

	private BigInteger chain_identifier;

	private String name;

	private String shortname;



}
