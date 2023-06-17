/**
 *
 */
package com.plgchain.app.plingaHelper.util.blockscout.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EthRpcResponse implements Serializable {

	private static final long serialVersionUID = 7923132773957083024L;

	private Long id;
	private String jsonrpc;
	private String result;


}
