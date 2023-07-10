/**
 *
 */
package com.plgchain.app.general;

import java.io.Serializable;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.plgchain.app.plingaHelper.util.NumberUtil;

/**
 *
 */
public class NumberUtilTest implements Serializable {

	private static final long serialVersionUID = 398249001380270381L;

	@Test
	public void generateRandomTestCase() {
		System.out.println("Random is : " + NumberUtil.generateRandomNumber(new BigDecimal(10), new BigDecimal(2000), 0));
	}

}
