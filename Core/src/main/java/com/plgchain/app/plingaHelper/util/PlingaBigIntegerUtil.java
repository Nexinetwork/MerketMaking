/**
 *
 */
package com.plgchain.app.plingaHelper.util;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author eae966
 *
 */
public class PlingaBigIntegerUtil implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8879527703340455116L;

	public static String minPrivateKeyHex = "0000000000000000000000000000000000000000000000000000000000000001";
	public static String maxPrivateKeyHex = "fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364140";
	public static int privateKeyHexLength = 64;

	public static BigInteger getRandomNumber(BigInteger minLimit, BigInteger maxLimit) {
		Random randNum = new Random();
		int len = maxLimit.bitLength();
		BigInteger res = new BigInteger(len, randNum);
		if (res.compareTo(minLimit) < 0)
			res = res.add(minLimit);
		if (res.compareTo(maxLimit) >= 0)
			res = res.mod(maxLimit).add(minLimit);
		return res;
	}

	public static BigInteger getMinPrivateKeyBigInt() {
		return getBigIntegerOfHex(minPrivateKeyHex);
	}

	public static BigInteger getMaxPrivateKeyBigInt() {
		return getBigIntegerOfHex(maxPrivateKeyHex);
	}

	public static BigInteger getMiddlePrivateKeyBigInt() {
		return getBigIntegerOfHex(maxPrivateKeyHex).divide(new BigInteger("2"));
	}

	public static BigInteger getRandomPrivateKeyBigInt() {
		return getRandomNumber(getMinPrivateKeyBigInt(), getMaxPrivateKeyBigInt());
	}

	public static List<BigInteger> getRandomPrivateKeyBigIntList(int count) {
		List<BigInteger> lst = new ArrayList<BigInteger>();
		for (int i=0;i<count;i++)
			lst.add(getRandomNumber(getMinPrivateKeyBigInt(), getMaxPrivateKeyBigInt()));
		return lst;
	}

	public static BigInteger getBigIntegerOfHex(String hex) {
		return new BigInteger(hex,16);
	}

	public static String getHexOfBigInteger(BigInteger bigInt) {
		return bigInt.toString(16);
	}

	public static String correctLenfthOfPrivateKey(String privateKey) {
		return String.format("%0"+ (64 - privateKey.length() )+"d%s",0 ,privateKey);
	}

	public static String correctLenfthOfPrivateKey(BigInteger bigInt) {
		String privateKey = getHexOfBigInteger(bigInt);
		for (int i = privateKey.length() ; i < 64 ; i ++)
			privateKey = "0" + privateKey;
		return privateKey;
	}

	public static BigInteger addInteger(BigInteger bigInt,int num) {
		return bigInt.add(new BigInteger(String.valueOf(num)));
	}

	public static BigInteger subtractInteger(BigInteger bigInt,int num) {
		return bigInt.subtract(new BigInteger(String.valueOf(num)));
	}

	public static BigInteger avarage(BigInteger bigInt1,BigInteger bigInt2) {
		BigInteger bigInt = bigInt1.add(bigInt2);
		return bigInt.divide(new BigInteger("2"));
	}

}
