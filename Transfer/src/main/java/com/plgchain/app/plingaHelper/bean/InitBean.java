/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 */
@Component
@Data
@ToString
@NoArgsConstructor
public class InitBean implements Serializable {

	private static final long serialVersionUID = 2792293419508311430L;

	private final static Logger logger = LoggerFactory.getLogger(InitBean.class);

	private Set<String> lockedMethod = new HashSet<String>();

	private HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

	private int jpaBatchCount = 2000;

	private int fixTransferWalletBalancePerRound = 500;

	private int transferPerRound = 1000;

	private BigDecimal minMaincoinInContractWallet = new BigDecimal(2);

	private BigDecimal maxMaincoinInContractWallet = new BigDecimal(10);

	private int decimalMaincoinInContractWallet = 2;

	private int selectPageSize = 20000;

	@PostConstruct
	public void init() {

	}

	public boolean doesActionRunning(String action) {
		return lockedMethod.contains(action);
	}

	public void startActionRunning(String action) {
		lockedMethod.add(action);
	}

	public void stopActionRunning(String action) {
		lockedMethod.remove(action);
	}

}
