/**
 *
 */
package com.plgchain.app.plingaHelper.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 */
public class NumberUtil implements Serializable {

	private static final long serialVersionUID = 2514435061294644162L;

	public static BigDecimal generateRandomNumber(BigDecimal min, BigDecimal max, int decimalPlaces) {
        BigDecimal randomNumber = min.add(new BigDecimal(Math.random()).multiply(max.subtract(min)));

        randomNumber = randomNumber.setScale(decimalPlaces, RoundingMode.HALF_UP);

        return randomNumber;
    }

	public static long generateRandomNumber(long min, long max, int decimal) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // Generate a random long between min and max
        long randomLong = random.nextLong(min, max + 1);

        // Generate a random decimal between 0 and 1
        BigDecimal randomDecimal = new BigDecimal(random.nextDouble());

        // Scale the random decimal according to the decimal parameter
        BigDecimal scaledDecimal = randomDecimal.multiply(BigDecimal.TEN.pow(decimal)).setScale(decimal, RoundingMode.DOWN);

        // Combine the random long and scaled decimal to get the final random number
        BigDecimal randomValue = new BigDecimal(randomLong).add(scaledDecimal);

        return randomValue.longValue();
    }

	public static int generateRandomNumber(int min, int max, int decimal) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // Generate a random long between min and max
        long randomLong = random.nextLong(min, max + 1);

        // Generate a random decimal between 0 and 1
        BigDecimal randomDecimal = new BigDecimal(random.nextDouble());

        // Scale the random decimal according to the decimal parameter
        BigDecimal scaledDecimal = randomDecimal.multiply(BigDecimal.TEN.pow(decimal)).setScale(decimal, RoundingMode.DOWN);

        // Combine the random long and scaled decimal to get the final random number
        BigDecimal randomValue = new BigDecimal(randomLong).add(scaledDecimal);

        return randomValue.intValue();
    }

}
