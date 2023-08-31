/**
 *
 */
package com.plgchain.app.sucurity;

import java.io.Serializable;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.plgchain.app.plingaHelper.util.SecurityUtil;

/**
 *
 */
public class SecurityUtilTest implements Serializable {

	private static final long serialVersionUID = -6522753024538832901L;


	@Test
	public void createWalletPidTestCase() {
		IntStream.range(0, 100).forEach(i -> {
		    System.out.println(SecurityUtil.generateRandomString(128));
		});
	}

}
