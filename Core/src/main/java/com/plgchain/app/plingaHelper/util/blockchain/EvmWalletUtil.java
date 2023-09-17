package com.plgchain.app.plingaHelper.util.blockchain;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;

import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.dto.EvmWalletDto;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMakingWallet;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EvmWalletUtil implements Serializable {

	private static final long serialVersionUID = 5331821388368101885L;

	private static final BigInteger MIN_PRIVATE_KEY = new BigInteger(
			"0000000000000000000000000000000000000000000000000000000000000001", 16);
	private static final BigInteger MAX_PRIVATE_KEY = new BigInteger(
			"fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364140", 16);
	private static final int PRIVATE_KEY_LENGTH = 64;

	private static final SecureRandom RANDOM = new SecureRandom();

	public static BigInteger getRandomNumber(BigInteger minLimit, BigInteger maxLimit) {
		BigInteger range = maxLimit.subtract(minLimit).add(BigInteger.ONE);
		BigInteger randomNumber = new BigInteger(range.bitLength(), RANDOM);

		if (randomNumber.compareTo(range) >= 0) {
			randomNumber = randomNumber.mod(range);
		}

		return randomNumber.add(minLimit);
	}

	public static BigInteger getBigIntegerOfHex(String hex) {
		return new BigInteger(hex, 16);
	}

	public static String getHexOfBigInteger(BigInteger bigInt) {
		return bigInt.toString(16);
	}

	public static String correctLengthOfPrivateKey(BigInteger bigInt) {
		String privateKey = getHexOfBigInteger(bigInt);
		return String.format("%0" + PRIVATE_KEY_LENGTH + "d", new BigInteger(privateKey, 16));
	}

	public static BigInteger addInteger(BigInteger bigInt, int num) {
		return bigInt.add(BigInteger.valueOf(num));
	}

	public static BigInteger getMinPrivateKeyBigInt() {
		return MIN_PRIVATE_KEY;
	}

	public static BigInteger getMaxPrivateKeyBigInt() {
		return MAX_PRIVATE_KEY;
	}

	public static BigInteger getMiddlePrivateKeyBigInt() {
		return MAX_PRIVATE_KEY.divide(BigInteger.TWO);
	}

	public static BigInteger getRandomPrivateKeyBigInt() {
		return getRandomNumber(getMinPrivateKeyBigInt(), getMaxPrivateKeyBigInt());
	}

	public static List<BigInteger> getRandomPrivateKeyBigIntList(int count) {
		return IntStream.range(0, count)
				.mapToObj(i -> getRandomNumber(getMinPrivateKeyBigInt(), getMaxPrivateKeyBigInt()))
				.collect(Collectors.toList());
	}

	public static BigInteger subtractInteger(BigInteger bigInt, int num) {
		return bigInt.subtract(BigInteger.valueOf(num));
	}

	public static BigInteger average(BigInteger bigInt1, BigInteger bigInt2) {
		return bigInt1.add(bigInt2).divide(BigInteger.TWO);
	}

	public static String correctLengthOfPrivateKey(String privateKey) {
		return String.format("%0" + (PRIVATE_KEY_LENGTH - privateKey.length()) + "d%s", 0, privateKey);
	}

	public static EvmWalletDto generateWallet(BigInteger bigInt) {
		String privateKey = correctLengthOfPrivateKey(bigInt);
		String publicKey = Credentials.create(ECKeyPair.create(bigInt)).getAddress().trim();
		String hexKey = getHexOfBigInteger(bigInt);
		return EvmWalletDto.builder().bigInt(bigInt).publicKey(publicKey).privateKey(privateKey).hexKey(hexKey).nonce(BigInteger.ZERO)
				.balance(BigDecimal.ZERO).build();
	}

	public static EvmWalletDto generateRandomWallet() {
		return generateWallet(getRandomPrivateKeyBigInt());
	}

	public static List<EvmWalletDto> generateRandomWallet(int count) {
		List<BigInteger> randomPrivateKeyList = getRandomPrivateKeyBigIntList(count);
		return randomPrivateKeyList.stream().map(EvmWalletUtil::generateWallet).collect(Collectors.toList());
	}

	public static List<MarketMakingWallet> convertWalletType(List<EvmWalletDto> lst, SmartContract contract,
			Blockchain blockchain, Coin coin, WalletType walletType) {
		return lst.stream()
				.map(w -> MarketMakingWallet.builder().balance(w.getBalance()).blockchain(blockchain).coin(coin)
						.contract(contract).contractAddress(contract.getContractsAddress())
						.privateKey(w.getPrivateKey()).publicKey(w.getPublicKey()).walletType(walletType).build())
				.collect(Collectors.toList());
	}


}
