/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.google.common.base.Strings;
import com.google.common.net.InetAddresses;
import com.plgchain.app.plingaHelper.annotation.LogMethod;
import com.plgchain.app.plingaHelper.annotation.UpdateBlockchainData;
import com.plgchain.app.plingaHelper.constant.AdminCommandType;
import com.plgchain.app.plingaHelper.constant.SysConstant;
import com.plgchain.app.plingaHelper.dto.BlockchainNodeDto;
import com.plgchain.app.plingaHelper.dto.EvmWalletDto;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.BlockchainNode;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMaking;
import com.plgchain.app.plingaHelper.exception.RestActionError;
import com.plgchain.app.plingaHelper.service.BlockchainNodeService;
import com.plgchain.app.plingaHelper.service.BlockchainService;
import com.plgchain.app.plingaHelper.service.CoinService;
import com.plgchain.app.plingaHelper.service.MarketMakingService;
import com.plgchain.app.plingaHelper.service.MarketMakingWalletService;
import com.plgchain.app.plingaHelper.service.SmartContractService;
import com.plgchain.app.plingaHelper.service.TankhahWalletService;
import com.plgchain.app.plingaHelper.type.CommandToRun;
import com.plgchain.app.plingaHelper.type.request.CoinReq;
import com.plgchain.app.plingaHelper.type.request.ContractReq;
import com.plgchain.app.plingaHelper.type.request.MarketMakingReq;
import com.plgchain.app.plingaHelper.type.request.SmartContractReq;
import com.plgchain.app.plingaHelper.type.response.TankhahWalletRes;
import com.plgchain.app.plingaHelper.util.blockchain.EvmWalletUtil;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 *
 */
@Component
public class BlockchainBean implements Serializable {

	private static final long serialVersionUID = -2749816508506842832L;

	private static final Logger logger = LoggerFactory.getLogger(BlockchainBean.class);

	@Inject
	private BlockchainService blockchainService;

	@Inject
	private CoinService coinService;

	@Inject
	private SmartContractService smartContractService;

	@Inject
	private BlockchainNodeService blockchainNodeService;

	@Inject
	private KafkaTemplate<String, String> kafkaTemplate;

	@Inject
	private MarketMakingService marketMakingService;

	@Inject
	private TankhahWalletService tankhahWalletService;

	@Inject
	private MarketMakingWalletService marketMakingWalletService;

	@LogMethod
	public Blockchain createBlockchain(Blockchain blockchain) throws RestActionError {
		if (blockchain == null)
			throw new RestActionError("Blockchain is Null");
		if (Strings.isNullOrEmpty(blockchain.getName()))
			throw new RestActionError("Blockchain name is empty");
		if (Strings.isNullOrEmpty(blockchain.getMainCoin()))
			throw new RestActionError("Blockchain main coin is empty");
		if (Strings.isNullOrEmpty(blockchain.getBlockExplorer()))
			throw new RestActionError("Blockchain Blockexplorer is empty");
		if (blockchain.getBlockchainType() == null)
			throw new RestActionError("Blockchain explorer is empty");
		if (blockchain.isEvm()) {
			if (blockchain.getChainId() == null)
				throw new RestActionError("ChainId for evm blockchain should not be empty");
			if (blockchain.getChainId().compareTo(BigInteger.ZERO) <= 0)
				throw new RestActionError("ChainId for evm blockchain should be bigger than 0");
			if (blockchainService.existsBlockchainByChainId(blockchain.getChainId())) {
				throw new RestActionError("ChainId for evm blockchain Already exist");
			}
		}
		if (blockchainService.existsBlockchainByName(blockchain.getName().trim())) {
			throw new RestActionError("Blockchain name already exist");
		}
		if (blockchainService.existsBlockchainByMainCoin(blockchain.getMainCoin().trim())) {
			throw new RestActionError("Blockchain main coin already exist");
		}
		blockchain.setName(blockchain.getName().trim());
		blockchain.setMainCoin(blockchain.getMainCoin().trim());
		blockchain.setBlockDuration(2);
		blockchain.setEnabled(true);
		blockchain.setHealthy(true);
		blockchain.setNodeCount(0);
		blockchain.setHeight(BigInteger.ZERO);
		blockchain = blockchainService.save(blockchain);
		CommandToRun ctr = new CommandToRun();
		ctr.setAdminCommandType(AdminCommandType.UPDATEBLOCKCHAIN);
		kafkaTemplate.send(SysConstant.KAFKA_ADMIN_COMMAND, JSON.toJSONString(ctr));
		return blockchain;
	}

	@LogMethod
	@UpdateBlockchainData
	public BlockchainNode createBlockchainNode(BlockchainNode blockchainNode) throws RestActionError {
		if (blockchainNode == null)
			throw new RestActionError("Blockchain node is Null");
		if (blockchainNode.getBlockchainId() == null)
			throw new RestActionError("Blockchain is Null");
		if (blockchainNode.getBlockchainId() <= 0)
			throw new RestActionError("Blockchain is Null");
		if (blockchainNode.getNodeType() == null)
			throw new RestActionError("Nodetype is Null");
		if (Strings.isNullOrEmpty(blockchainNode.getRpcUrl()))
			throw new RestActionError("RpcUrl is Null");
		if (Strings.isNullOrEmpty(blockchainNode.getServiceNeme()))
			throw new RestActionError("Servicename is Null");
		if (Strings.isNullOrEmpty(blockchainNode.getServerIp()))
			throw new RestActionError("ServerIp is Null");
		if (!InetAddresses.isInetAddress(blockchainNode.getServerIp()))
			throw new RestActionError("Invalid ServerIp");
		if (!blockchainService.existById(blockchainNode.getBlockchainId()))
			throw new RestActionError("Blockchain does not exist");
		blockchainNode.setBlockchain(blockchainService.findById(blockchainNode.getBlockchainId()).get());
		if (blockchainNode.getSshPort() == null)
			blockchainNode.setSshPort(22);
		if (blockchainNode.getSshPort() <= 0)
			blockchainNode.setSshPort(22);
		blockchainNode.setLastBlock(BigInteger.ZERO);
		blockchainNode = blockchainNodeService.save(blockchainNode);
		return blockchainNode;
	}

	@LogMethod
	@UpdateBlockchainData
	public BlockchainNode createBlockchainNode(BlockchainNodeDto blockchainNode) throws RestActionError {
		if (blockchainNode == null)
			throw new RestActionError("Blockchain node is Null");
		if (blockchainNode.isBlockchainNull())
			throw new RestActionError("Blockchain is Null");
		if (blockchainNode.getNodeType() == null)
			throw new RestActionError("Nodetype is Null");
		if (Strings.isNullOrEmpty(blockchainNode.getRpcUrl()))
			throw new RestActionError("RpcUrl is Null");
		if (Strings.isNullOrEmpty(blockchainNode.getServiceNeme()))
			throw new RestActionError("Servicename is Null");
		if (Strings.isNullOrEmpty(blockchainNode.getServerIp()))
			throw new RestActionError("ServerIp is Null");
		if (!InetAddresses.isInetAddress(blockchainNode.getServerIp()))
			throw new RestActionError("Invalid ServerIp");
		if (!existBlockchain(blockchainNode.getBlockchainId(), blockchainNode.getBlockchain()))
			throw new RestActionError("Blockchain does not exist");
		if (blockchainNode.getSshPort() == null)
			blockchainNode.setSshPort(22);
		if (blockchainNode.getSshPort() <= 0)
			blockchainNode.setSshPort(22);
		blockchainNode.setLastBlock(BigInteger.ZERO);
		var node = BlockchainNode.builder()
				.blockchain(findBlockchain(blockchainNode.getBlockchainId(), blockchainNode.getBlockchain()).get())
				.nodeType(blockchainNode.getNodeType()).rpcUrl(blockchainNode.getRpcUrl())
				.serverIp(blockchainNode.getServerIp()).serviceNeme(blockchainNode.getServiceNeme())
				.sshPort(blockchainNode.getSshPort()).validator(blockchainNode.isValidator())
				.enabled(blockchainNode.isEnabled()).mustCheck(blockchainNode.isMustCheck()).lastBlock(BigInteger.ZERO)
				.build();
		node = blockchainNodeService.save(node);
		return node;
	}

	public boolean existBlockchain(Long blockchainId, String blockchainName) {
		if (blockchainId != null && blockchainId > 0)
			return blockchainService.existById(blockchainId);
		if (!Strings.isNullOrEmpty(blockchainName))
			return blockchainService.existsBlockchainByName(blockchainName);
		return false;
	}

	public Optional<Blockchain> findBlockchain(Long blockchainId, String blockchainName) {
		if (blockchainId != null && blockchainId > 0)
			return blockchainService.findById(blockchainId);
		if (!Strings.isNullOrEmpty(blockchainName))
			return blockchainService.findByName(blockchainName);
		return null;
	}

	@LogMethod
	@UpdateBlockchainData
	public SmartContract createSmartContract(ContractReq contractReq) throws RestActionError {
		if (contractReq == null)
			throw new RestActionError("Contract Object is Null");
		if (Strings.isNullOrEmpty(contractReq.getBlockchainCoingeckoId()))
			throw new RestActionError("Blockchain is blank");
		if (Strings.isNullOrEmpty(contractReq.getCoinCoingeckoId()))
			throw new RestActionError("Coin is blank");
		if (Strings.isNullOrEmpty(contractReq.getContract()))
			throw new RestActionError("Contract is blank");
		if (!blockchainService.existsBlockchainByCoingeckoId(contractReq.getBlockchainCoingeckoId()))
			throw new RestActionError("Blockchain does not exist");
		if (!coinService.existsCoinByCoingeckoId(contractReq.getCoinCoingeckoId()))
			throw new RestActionError("Coin does not exist");
		Blockchain blockchain = blockchainService.findByCoingeckoId(contractReq.getBlockchainCoingeckoId()).get();
		Coin coin = coinService.findByCoingeckoId(contractReq.getCoinCoingeckoId()).get();
		if (smartContractService.existsSmartContractByBlockchainAndCoin(blockchain, coin))
			throw new RestActionError("Coin exist on blockchain");
		var sm = SmartContract.builder().coin(coin).blockchain(blockchain).contractsAddress(contractReq.getContract())
				.decimal(contractReq.getDecimal() != null ? contractReq.getDecimal() : 18)
				.mustCheck(contractReq.isMustCheck()).isMain(contractReq.isMustCheck()).mustAdd(contractReq.isMustAdd())
				.build();
		sm = smartContractService.save(sm);
		return sm;
	}

	@LogMethod
	@UpdateBlockchainData
	public Coin createNewCoin(CoinReq coin) throws RestActionError {
		if (coin == null)
			throw new RestActionError("Coin Object is Null");
		if (Strings.isNullOrEmpty(coin.getSymbol()))
			throw new RestActionError("Symbol is blank");
		if (Strings.isNullOrEmpty(coin.getName()))
			throw new RestActionError("Coin name is blank");
		var coinRes = Coin.builder().coingeckoId(coin.getCoingeckoId()).mustCheck(coin.isMustCheck())
				.symbol(coin.getSymbol()).priceInUsd(coin.getPriceInUsd()).listed(coin.isListed()).name(coin.getName())
				.build();
		coinRes = coinService.save(coinRes);
		return coinRes;
	}

	@LogMethod
	@UpdateBlockchainData
	public SmartContract createOrUpdateSmartContract(SmartContractReq sc) throws RestActionError {
		var smartContract = new SmartContract();
		if (sc == null)
			throw new RestActionError("Smartcontract is null");
		if (sc.getContractId() <= 0 && Strings.isNullOrEmpty(sc.getBlockchain()) && sc.getBlockchainId() <= 0)
			throw new RestActionError("Blockchain is null");
		if (sc.getContractId() <= 0 && Strings.isNullOrEmpty(sc.getCoin()) && sc.getCoinId() <= 0)
			throw new RestActionError("Coin is null");
		if (sc.getContractId() > 0) {
			if (!smartContractService.existById(sc.getContractId()))
				throw new RestActionError("Invalid Smart Contract");
			else
				smartContract = smartContractService.findById(sc.getContractId()).get();
		}
		if (Strings.isNullOrEmpty(sc.getContractsAddress()))
			if (Strings.isNullOrEmpty(smartContract.getContractsAddress()))
				throw new RestActionError("Contract Address is blank");
		if (!Strings.isNullOrEmpty(sc.getContractsAddress()))
			smartContract.setContractsAddress(sc.getContractsAddress());
		if (smartContract.getBlockchain() == null) {
			if (sc.getBlockchainId() > 0) {
				if (!blockchainService.existById(sc.getBlockchainId()))
					throw new RestActionError("Invalid blockchain");
				Blockchain blockchain = blockchainService.findById(sc.getBlockchainId()).get();
				smartContract.setBlockchain(blockchain);
			} else {
				if (!blockchainService.existsBlockchainByName(sc.getBlockchain()))
					throw new RestActionError("Invalid blockchain");
				Blockchain blockchain = blockchainService.findByName(sc.getBlockchain()).get();
				smartContract.setBlockchain(blockchain);
			}
		}
		if (smartContract.getCoin() == null) {
			if (sc.getCoinId() > 0) {
				if (!coinService.existById(sc.getCoinId()))
					throw new RestActionError("Invalid Coin");
				Coin coin = coinService.findById(sc.getCoinId()).get();
				smartContract.setCoin(coin);
			} else {
				if (!coinService.existsCoinByCoingeckoId(sc.getCoin()))
					throw new RestActionError("Invalid Coin");
				Coin coin = coinService.findByCoingeckoId(sc.getCoin()).get();
				smartContract.setCoin(coin);
			}
		}
		if (sc.getDecimal() <= 0) {
			if (smartContract.getDecimal() <= 0) {
				smartContract.setDecimal(18);
			}
		} else {
			smartContract.setDecimal(sc.getDecimal());
		}
		smartContract.setMain(sc.isMain());
		smartContract.setMarketMaking(sc.isMarketMaking());
		smartContract.setMustAdd(sc.isMustAdd());
		smartContract.setMustCheck(sc.isMustCheck());
		smartContract = smartContractService.save(smartContract);
		return smartContract;
	}

	@LogMethod
	@UpdateBlockchainData
	public MarketMaking createOrUpdateMarketMaking(MarketMakingReq mmReq) throws RestActionError {
		if (mmReq == null) {
			throw new RestActionError("Marketmaking is null");
		}

		long smartContractId = mmReq.getSmartContractId();
		if (smartContractId <= 0) {
			throw new RestActionError("Smartcontract is null");
		}

		SmartContract smartContract = smartContractService.findById(smartContractId)
				.orElseThrow(() -> new RestActionError("Invalid smartcontract"));

		MarketMaking mm = smartContract.getMarketMakingObject();
		if (mm == null) {
		    mm = MarketMaking.builder()
		        .smartContract(smartContract)
		        .currentTransferWalletCount(0)
		        .initialWalletCreationDone(false)
		        .initialWalletFundingDone(false)
		        .build();
		}

		mm.setDailyAddWallet(mmReq.getDailyAddWallet());
		mm.setInitialDecimal(mmReq.getInitialDecimal());
		mm.setInitialWallet(mmReq.getInitialWallet());
		mm.setMaxInitial(mmReq.getMaxInitial());
		mm.setMinInitial(mmReq.getMinInitial());
		mm.setTransactionParallelType(mmReq.getTransactionParallelType());

		mm = marketMakingService.save(mm);
		return mm;
	}

	@LogMethod
	@UpdateBlockchainData
	@Transactional
	public SmartContract getSmartContract(SmartContractReq smReq) throws RestActionError {
		if (smReq == null) {
			throw new RestActionError("SmartContract Object is null");
		}

		if (smReq.getContractId() > 0) {
			return smartContractService.findById(smReq.getContractId())
					.orElseThrow(() -> new RestActionError("Invalid SmartContract"));
		}

		if (Strings.isNullOrEmpty(smReq.getContractsAddress())) {
			throw new RestActionError("Contract address is null");
		}

		if (smReq.getBlockchainId() > 0) {
			return blockchainService.findById(smReq.getBlockchainId())
					.flatMap(blockchain -> smartContractService.findByBlockchainAndContractsAddress(blockchain,
							smReq.getContractsAddress()))
					.orElseThrow(() -> new RestActionError("Invalid Contract address in blockchain"));
		}

		if (!Strings.isNullOrEmpty(smReq.getBlockchain())) {
			return blockchainService.findByName(smReq.getBlockchain())
					.flatMap(blockchain -> smartContractService.findByBlockchainAndContractsAddress(blockchain,
							smReq.getContractsAddress()))
					.orElseThrow(() -> new RestActionError("Invalid Contract address in blockchain"));
		}

		throw new RestActionError("SmartContract Not found");
	}

	@Transactional
	public List<TankhahWalletRes> getTankhahWalletListAsResult() {
		return tankhahWalletService.findAll().stream().map(th -> TankhahWalletRes.builder().balance(th.getBalance())
				.blockchain(th.getContract().getBlockchain().getName())
				.blockchainId(th.getContract().getBlockchain().getBlockchainId())
				.coinId(th.getContract().getCoin().getCoinId()).coinName(th.getContract().getCoin().getName())
				.coinSymbol(th.getContract().getCoin().getSymbol())
				.contractAddress(th.getContract().getContractsAddress()).contractId(th.getContract().getContractId())
				.privateKey(th.getPrivateKey()).publicKey(th.getPublicKey()).tankhahWalletId(th.getTankhahWalletId())
				.tankhahWalletType(th.getTankhahWalletType().name()).build()).collect(Collectors.toList());
	}

	public void fixWalletPrivatekeys() {
	    var tankhahWallets = tankhahWalletService.findAll();
	    var marketMakingWallets = marketMakingWalletService.findAll();

	    var tankhahWalletsToUpdate = tankhahWallets.stream()
	            .filter(wallet -> Strings.isNullOrEmpty(wallet.getPrivateKeyHex()))
	            .peek(wallet -> {
	                EvmWalletDto wDto = EvmWalletUtil.generateWallet(new BigInteger(wallet.getPrivateKey()));
	                wallet.setPrivateKeyHex(wDto.getHexKey());
	                logger.info(String.format("Private key of tankhahwallet %s has been set to %s.",
	                        wallet.getPublicKey(), wallet.getPrivateKeyHex()));
	            })
	            .collect(Collectors.toList());

	    var marketMakingWalletsToUpdate = marketMakingWallets.stream()
	            .filter(wallet -> Strings.isNullOrEmpty(wallet.getPrivateKeyHex()))
	            .peek(wallet -> {
	                EvmWalletDto wDto = EvmWalletUtil.generateWallet(new BigInteger(wallet.getPrivateKey()));
	                wallet.setPrivateKeyHex(wDto.getHexKey());
	                logger.info(String.format("Private key of Transfer Wallet %s has been set to %s.",
	                        wallet.getPublicKey(), wallet.getPrivateKeyHex()));
	            })
	            .collect(Collectors.toList());

	    if (!tankhahWalletsToUpdate.isEmpty()) {
	        tankhahWalletService.saveAll(tankhahWalletsToUpdate);
	    }

	    if (!marketMakingWalletsToUpdate.isEmpty()) {
	        marketMakingWalletService.saveAll(marketMakingWalletsToUpdate);
	    }

	    logger.info("Fixing wallet has been done");
	}


}
