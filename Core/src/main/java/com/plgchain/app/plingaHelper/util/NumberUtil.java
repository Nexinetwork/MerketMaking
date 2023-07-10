/**
 *
 */
package com.plgchain.app.plingaHelper.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 *
 */
public class NumberUtil implements Serializable {

	private static final long serialVersionUID = 2514435061294644162L;

	public static BigDecimal generateRandomNumber(BigDecimal min, BigDecimal max, int decimalPlaces) {
        Random random = new Random();
        BigDecimal randomNumber = min.add(new BigDecimal(Math.random()).multiply(max.subtract(min)));

        randomNumber = randomNumber.setScale(decimalPlaces, RoundingMode.HALF_UP);

        return randomNumber;
    }

}
