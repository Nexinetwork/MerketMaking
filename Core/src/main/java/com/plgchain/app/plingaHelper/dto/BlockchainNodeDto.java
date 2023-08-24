/**
 *
 */
package com.plgchain.app.plingaHelper.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.Strings;
import com.plgchain.app.plingaHelper.constant.BlockchainNodeType;
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
@Data
@ToString
public class BlockchainNodeDto implements Serializable {

	private static final long serialVersionUID = 9180027018646071770L;

	private String blockchain;

	private BlockchainNodeType nodeType;

	private String rpcUrl;

	private BigInteger lastBlock;

	private boolean mmNode;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime lastCheck;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime creationDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
	private LocalDateTime lastUpdateDate;

	private String serverIp;

	private Integer sshPort;

	private String serviceNeme;

	private boolean validator;

	private Long blockchainId;

	private boolean enabled;

	private boolean mustCheck;

	public boolean isBlockchainNull() {
		return ((blockchainId == null || blockchainId <= 0) && Strings.isNullOrEmpty(blockchain));
	}




}
