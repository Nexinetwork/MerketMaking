/**
 *
 */
package com.plgchain.app.plingaHelper.bean;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.google.common.base.Strings;
import com.google.common.net.InetAddresses;
import com.plgchain.app.plingaHelper.annotation.LogMethod;
import com.plgchain.app.plingaHelper.annotation.UpdateBlockchainData;
import com.plgchain.app.plingaHelper.constant.AdminCommandType;
import com.plgchain.app.plingaHelper.constant.BlockchainNodeType;
import com.plgchain.app.plingaHelper.constant.SysConstant;
import com.plgchain.app.plingaHelper.dto.BlockchainNodeDto;
import com.plgchain.app.plingaHelper.dto.EvmWalletDto;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.BlockchainNode;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMaking;
import com.plgchain.app.plingaHelper.exception.RestActionError;
import com.plgchain.app.plingaHelper.microService.BlockchainNodeMicroService;
import com.plgchain.app.plingaHelper.microService.BlockchainMicroService;
import com.plgchain.app.plingaHelper.microService.CoinMicroService;
import com.plgchain.app.plingaHelper.microService.MarketMakingMicroService;
import com.plgchain.app.plingaHelper.microService.MarketMakingWalletMicroService;
import com.plgchain.app.plingaHelper.microService.SmartContractMicroService;
import com.plgchain.app.plingaHelper.microService.SystemConfigMicroService;
import com.plgchain.app.plingaHelper.microService.TankhahWalletMicroService;
import com.plgchain.app.plingaHelper.type.CommandToRun;
import com.plgchain.app.plingaHelper.type.request.CoinReq;
import com.plgchain.app.plingaHelper.type.request.ContractReq;
import com.plgchain.app.plingaHelper.type.request.GeneralReq;
import com.plgchain.app.plingaHelper.type.request.MarketMakingReq;
import com.plgchain.app.plingaHelper.type.request.SmartContractReq;
import com.plgchain.app.plingaHelper.type.response.TankhahWalletRes;
import com.plgchain.app.plingaHelper.util.ArrayListHelper;
import com.plgchain.app.plingaHelper.util.SecurityUtil;
import com.plgchain.app.plingaHelper.util.ServiceUtil;
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
	private BlockchainMicroService blockchainMicroService;

	@Inject
	private CoinMicroService coinMicroService;

	@Inject
	private SmartContractMicroService smartContractMicroService;

	@Inject
	private BlockchainNodeMicroService blockchainNodeMicroService;

	@Inject
	private KafkaTemplate<String, String> kafkaTemplate;

	@Inject
	private MarketMakingMicroService marketMakingMicroService;

	@Inject
	private TankhahWalletMicroService tankhahWalletMicroService;

	@Inject
	private MarketMakingWalletMicroService marketMakingWalletMicroService;

	@Inject
	private SystemConfigMicroService systemConfigMicroService;

	@Inject
	private CommonInitBean commonInitBean;

	@SuppressWarnings("rawtypes")
	@Inject
	private RedisTemplate redisTemplate;

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
			if (blockchainMicroService.existsBlockchainByChainId(blockchain.getChainId())) {
				throw new RestActionError("ChainId for evm blockchain Already exist");
			}
		}
		if (blockchainMicroService.existsBlockchainByName(blockchain.getName().trim())) {
			throw new RestActionError("Blockchain name already exist");
		}
		if (blockchainMicroService.existsBlockchainByMainCoin(blockchain.getMainCoin().trim())) {
			throw new RestActionError("Blockchain main coin already exist");
		}
		blockchain.setName(blockchain.getName().trim());
		blockchain.setMainCoin(blockchain.getMainCoin().trim());
		blockchain.setBlockDuration(2);
		blockchain.setEnabled(true);
		blockchain.setHealthy(true);
		blockchain.setNodeCount(0);
		blockchain.setHeight(BigInteger.ZERO);
		blockchain = blockchainMicroService.save(blockchain);
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
		if (!blockchainMicroService.existById(blockchainNode.getBlockchainId()))
			throw new RestActionError("Blockchain does not exist");
		blockchainNode.setBlockchain(blockchainMicroService.findById(blockchainNode.getBlockchainId()).get());
		if (blockchainNode.getSshPort() == null)
			blockchainNode.setSshPort(22);
		if (blockchainNode.getSshPort() <= 0)
			blockchainNode.setSshPort(22);
		blockchainNode.setLastBlock(BigInteger.ZERO);
		blockchainNode = blockchainNodeMicroService.save(blockchainNode);
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
		node = blockchainNodeMicroService.save(node);
		return node;
	}

	public boolean existBlockchain(Long blockchainId, String blockchainName) {
		if (blockchainId != null && blockchainId > 0)
			return blockchainMicroService.existById(blockchainId);
		if (!Strings.isNullOrEmpty(blockchainName))
			return blockchainMicroService.existsBlockchainByName(blockchainName);
		return false;
	}

	public Optional<Blockchain> findBlockchain(Long blockchainId, String blockchainName) {
		if (blockchainId != null && blockchainId > 0)
			return blockchainMicroService.findById(blockchainId);
		if (!Strings.isNullOrEmpty(blockchainName))
			return blockchainMicroService.findByName(blockchainName);
		return null;
	}

	@LogMethod
	@UpdateBlockchainData
	public SmartContract createSmartContract(ContractReq contractReq) throws RestActionError {
		if (contractReq == null)
			throw new RestActionError("Contract Object is Null");
		if (Strings.isNullOrEmpty(contractReq.getBlockchainCoingeckoId())
				&& Strings.isNullOrEmpty(contractReq.getBlockchainName())
				&& (contractReq.getBlockchainId() == null || contractReq.getBlockchainId() <= 0))
			throw new RestActionError("Blockchain is blank");
		if (Strings.isNullOrEmpty(contractReq.getCoinCoingeckoId()))
			throw new RestActionError("Coin is blank");
		if (Strings.isNullOrEmpty(contractReq.getContract()))
			throw new RestActionError("Contract is blank");
		if (!blockchainMicroService.existsBlockchainByCoingeckoId(contractReq.getBlockchainCoingeckoId()))
			throw new RestActionError("Blockchain does not exist");
		if (!coinMicroService.existsCoinByCoingeckoId(contractReq.getCoinCoingeckoId()))
			throw new RestActionError("Coin does not exist");
		Blockchain blockchain = blockchainMicroService.findByCoingeckoId(contractReq.getBlockchainCoingeckoId()).get();
		Coin coin = coinMicroService.findByCoingeckoId(contractReq.getCoinCoingeckoId()).get();
		if (smartContractMicroService.existsSmartContractByBlockchainAndCoin(blockchain, coin))
			throw new RestActionError("Coin exist on blockchain");
		var sm = SmartContract.builder().coin(coin).blockchain(blockchain).contractsAddress(contractReq.getContract())
				.decimal(contractReq.getDecimal() != null ? contractReq.getDecimal() : 18)
				.mustCheck(contractReq.isMustCheck()).isMain(contractReq.isMustCheck()).mustAdd(contractReq.isMustAdd())
				.build();
		sm = smartContractMicroService.save(sm);
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
		coinRes = coinMicroService.save(coinRes);
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
			if (!smartContractMicroService.existById(sc.getContractId()))
				throw new RestActionError("Invalid Smart Contract");
			else
				smartContract = smartContractMicroService.findById(sc.getContractId()).get();
		}
		if (Strings.isNullOrEmpty(sc.getContractsAddress()))
			if (Strings.isNullOrEmpty(smartContract.getContractsAddress()))
				throw new RestActionError("Contract Address is blank");
		if (!Strings.isNullOrEmpty(sc.getContractsAddress()))
			smartContract.setContractsAddress(sc.getContractsAddress());
		if (smartContract.getBlockchain() == null) {
			if (sc.getBlockchainId() > 0) {
				if (!blockchainMicroService.existById(sc.getBlockchainId()))
					throw new RestActionError("Invalid blockchain");
				Blockchain blockchain = blockchainMicroService.findById(sc.getBlockchainId()).get();
				smartContract.setBlockchain(blockchain);
			} else {
				if (!blockchainMicroService.existsBlockchainByName(sc.getBlockchain()))
					throw new RestActionError("Invalid blockchain");
				Blockchain blockchain = blockchainMicroService.findByName(sc.getBlockchain()).get();
				smartContract.setBlockchain(blockchain);
			}
		}
		if (smartContract.getCoin() == null) {
			if (sc.getCoinId() > 0) {
				if (!coinMicroService.existById(sc.getCoinId()))
					throw new RestActionError("Invalid Coin");
				Coin coin = coinMicroService.findById(sc.getCoinId()).get();
				smartContract.setCoin(coin);
			} else {
				if (!coinMicroService.existsCoinByCoingeckoId(sc.getCoin()))
					throw new RestActionError("Invalid Coin");
				Coin coin = coinMicroService.findByCoingeckoId(sc.getCoin()).get();
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
		smartContract = smartContractMicroService.save(smartContract);
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

		SmartContract smartContract = smartContractMicroService.findById(smartContractId)
				.orElseThrow(() -> new RestActionError("Invalid smartcontract"));

		MarketMaking mm = smartContract.getMarketMakingObject();
		if (mm == null) {
			mm = MarketMaking.builder().smartContract(smartContract).currentTransferWalletCount(0)
					.initialWalletCreationDone(false).initialWalletFundingDone(false)
					.trPid(SecurityUtil.generateRandomString(128)).dfPid(SecurityUtil.generateRandomString(128))
					.build();
		}

		mm.setDailyAddWallet(mmReq.getDailyAddWallet());
		mm.setInitialDecimal(mmReq.getInitialDecimal());
		mm.setInitialWallet(mmReq.getInitialWallet());
		mm.setMaxInitial(mmReq.getMaxInitial());
		mm.setMinInitial(mmReq.getMinInitial());
		mm.setInitialDefiWallet(mmReq.getInitialDefiWallet());
		mm.setMaxDefiInitial(mmReq.getMaxDefiInitial());
		mm.setMinDefiInitial(mmReq.getMinDefiInitial());
		mm.setTransactionParallelType(mmReq.getTransactionParallelType());
		mm.setMustUpdateMongoTransfer(mmReq.isMustUpdateMongoTransfer());
		mm.setMustUpdateMongoDefi(mmReq.isMustUpdateMongoDefi());
		mm = marketMakingMicroService.save(mm);
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
			return smartContractMicroService.findById(smReq.getContractId())
					.orElseThrow(() -> new RestActionError("Invalid SmartContract"));
		}

		if (Strings.isNullOrEmpty(smReq.getContractsAddress())) {
			throw new RestActionError("Contract address is null");
		}

		if (smReq.getBlockchainId() > 0) {
			return blockchainMicroService.findById(smReq.getBlockchainId())
					.flatMap(blockchain -> smartContractMicroService.findByBlockchainAndContractsAddress(blockchain,
							smReq.getContractsAddress()))
					.orElseThrow(() -> new RestActionError("Invalid Contract address in blockchain"));
		}

		if (!Strings.isNullOrEmpty(smReq.getBlockchain())) {
			return blockchainMicroService.findByName(smReq.getBlockchain())
					.flatMap(blockchain -> smartContractMicroService.findByBlockchainAndContractsAddress(blockchain,
							smReq.getContractsAddress()))
					.orElseThrow(() -> new RestActionError("Invalid Contract address in blockchain"));
		}

		throw new RestActionError("SmartContract Not found");
	}
	
	@Transactional
	public List<TankhahWalletRes> getTankhahWalletListByBlockchainAsResult(Long blockchainId, String blockchainName) {
		Optional<Blockchain> blockchain = findBlockchain(blockchainId, blockchainName);
		List<TankhahWalletRes> tankhahWalletResList = blockchain.map(b -> tankhahWalletMicroService.findByBlockchain(b).stream()
		        .map(th -> TankhahWalletRes.builder()
		                .balance(th.getBalance())
		                .blockchain(th.getContract().getBlockchain().getName())
		                .blockchainId(th.getContract().getBlockchain().getBlockchainId())
		                .coinId(th.getContract().getCoin().getCoinId())
		                .coinName(th.getContract().getCoin().getName())
		                .coinSymbol(th.getContract().getCoin().getSymbol())
		                .contractAddress(th.getContract().getContractsAddress())
		                .contractId(th.getContract().getContractId())
		                .privateKey(th.getPrivateKey())
		                .publicKey(th.getPublicKey())
		                .tankhahWalletId(th.getTankhahWalletId())
		                .tankhahWalletType(th.getTankhahWalletType().name())
		                .build())
		        .collect(Collectors.toList()))
		        .orElse(Collections.emptyList());

		return tankhahWalletResList;
	}

	@Transactional
	public List<TankhahWalletRes> getTankhahWalletListAsResult() {
		return tankhahWalletMicroService.findAll().stream().map(th -> TankhahWalletRes.builder()
				.balance(th.getBalance()).blockchain(th.getContract().getBlockchain().getName())
				.blockchainId(th.getContract().getBlockchain().getBlockchainId())
				.coinId(th.getContract().getCoin().getCoinId()).coinName(th.getContract().getCoin().getName())
				.coinSymbol(th.getContract().getCoin().getSymbol())
				.contractAddress(th.getContract().getContractsAddress()).contractId(th.getContract().getContractId())
				.privateKey(th.getPrivateKey()).publicKey(th.getPublicKey()).tankhahWalletId(th.getTankhahWalletId())
				.tankhahWalletType(th.getTankhahWalletType().name()).build()).collect(Collectors.toList());
	}

	public void fixWalletPrivatekeys() {
		var tankhahWallets = tankhahWalletMicroService.findAll();
		var marketMakingWallets = marketMakingWalletMicroService.findAll();

		var tankhahWalletsToUpdate = tankhahWallets.stream()
				.filter(wallet -> Strings.isNullOrEmpty(wallet.getPrivateKeyHex())).peek(wallet -> {
					EvmWalletDto wDto = EvmWalletUtil.generateWallet(new BigInteger(wallet.getPrivateKey()));
					wallet.setPrivateKeyHex(wDto.getHexKey());
					logger.info(String.format("Private key of tankhahwallet %s has been set to %s.",
							wallet.getPublicKey(), wallet.getPrivateKeyHex()));
				}).collect(Collectors.toList());

		var marketMakingWalletsToUpdate = marketMakingWallets.stream()
				.filter(wallet -> Strings.isNullOrEmpty(wallet.getPrivateKeyHex())).peek(wallet -> {
					EvmWalletDto wDto = EvmWalletUtil.generateWallet(new BigInteger(wallet.getPrivateKey()));
					wallet.setPrivateKeyHex(wDto.getHexKey());
					logger.info(String.format("Private key of Transfer Wallet %s has been set to %s.",
							wallet.getPublicKey(), wallet.getPrivateKeyHex()));
				}).collect(Collectors.toList());

		if (!tankhahWalletsToUpdate.isEmpty()) {
			tankhahWalletMicroService.saveAll(tankhahWalletsToUpdate);
		}

		if (!marketMakingWalletsToUpdate.isEmpty()) {
			marketMakingWalletMicroService.saveAll(marketMakingWalletsToUpdate);
		}

		logger.info("Fixing wallet has been done");
	}

	public Long deleteAllNodesBlockchainNodes(String blockchainName) throws RestActionError {
		if (Strings.isNullOrEmpty(blockchainName)) {
			throw new RestActionError("Blockchain is null.");
		}
		if (!blockchainMicroService.existsBlockchainByName(blockchainName)) {
			throw new RestActionError("Invalid Blockchain.");
		}
		Optional<Blockchain> bc = blockchainMicroService.findByName(blockchainName);
		return blockchainNodeMicroService.removeByBlockchain(bc.get());
	}

	@Transactional
	public void stopAllNodesOfBlockchain(String blockchainName) {
		Optional<Blockchain> bc = blockchainMicroService.findByName(blockchainName);
		bc.orElseThrow(() -> new RuntimeException("Blockchain not found"));
		var privateKey = systemConfigMicroService.findByConfigName("ssh-key-path").get().getConfigStringValue();
		bc.get().getNodeList().parallelStream().forEach(node -> {
			try {
				ServiceUtil.stopService(node.getServerIp(), node.getSshPort(), privateKey, node.getServiceNeme());
				logger.info(String.format("Server %s with service %s has been stopped.", node.getServerIp(),
						node.getServiceNeme()));
			} catch (Exception e) {
				logger.error(String.format("System stop error in ip %s/%s and service %s", node.getServerIp(),
						node.getSshPort(), node.getServiceNeme()));
			}
		});
	}

	@Transactional
	public void startAllNodesOfBlockchain(String blockchainName) {
		Optional<Blockchain> bc = blockchainMicroService.findByName(blockchainName);
		bc.orElseThrow(() -> new RuntimeException("Blockchain not found"));
		var privateKey = systemConfigMicroService.findByConfigName("ssh-key-path").get().getConfigStringValue();
		bc.get().getNodeList().parallelStream().forEach(node -> {
			try {
				ServiceUtil.startService(node.getServerIp(), node.getSshPort(), privateKey, node.getServiceNeme());
				logger.info(String.format("Server %s with service %s has been started.", node.getServerIp(),
						node.getServiceNeme()));
			} catch (Exception e) {
				logger.error(String.format("System start error in ip %s/%s and service %s", node.getServerIp(),
						node.getSshPort(), node.getServiceNeme()));
			}
		});
	}

	@Transactional
	public void restartAllNodesOfBlockchain(String blockchainName) {
		Optional<Blockchain> bc = blockchainMicroService.findByName(blockchainName);
		bc.orElseThrow(() -> new RuntimeException("Blockchain not found"));
		var privateKey = systemConfigMicroService.findByConfigName("ssh-key-path").get().getConfigStringValue();
		bc.get().getNodeList().parallelStream().forEach(node -> {
			try {
				ServiceUtil.restartService(node.getServerIp(), node.getSshPort(), privateKey, node.getServiceNeme());
				logger.info(String.format("Server %s with service %s has been restarted.", node.getServerIp(),
						node.getServiceNeme()));
			} catch (Exception e) {
				logger.error(String.format("System restart error in ip %s/%s and service %s", node.getServerIp(),
						node.getSshPort(), node.getServiceNeme()));
			}
		});
	}

	public void stopAndStartMMNode(Blockchain blockchain) {
		blockchain.getNodeList().stream().filter(node -> node.isMmNode()).forEach(node -> {
			try {
				ServiceUtil.stopService(node.getServerIp(), node.getSshPort(), commonInitBean.getPrivateKey(),
						node.getServiceNeme());
				ServiceUtil.startService(node.getServerIp(), node.getSshPort(), commonInitBean.getPrivateKey(),
						node.getServiceNeme());
				logger.info(String.format("Node %s has been restarted", node));
			} catch (Exception e) {
				logger.error("Error in restart blockchain", e);
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public void restartBlockchain(Blockchain blockchain) {
		if (!commonInitBean.doesBlockchainRestarting(blockchain.getName())) {
			commonInitBean.startBlockchainRestarting(blockchain.getName());
			logger.info(String.format("start restarting blockchain %s", blockchain.getName()));
			HashOperations<String, String, String> blockchainDataString = redisTemplate.opsForHash();
			Map<String, String> entries = blockchainDataString.entries(SysConstant.REDIS_NODE_DATA);
			entries.forEach((key, value) -> {
				ArrayListHelper.parseJsonToArrayList(value, BlockchainNode.class).stream()
						.filter(blchNode -> blchNode != null
								&& blchNode.getBlockchain().getName().equals(blockchain.getName())
								&& blchNode.getNodeType().equals(BlockchainNodeType.BLOCKCHAINNODE))
						.forEach(blockchainNode -> {
							logger.info(String.format("Try to stop Server %s with service %s.",
									blockchainNode.getServerIp(), blockchainNode.getServiceNeme()));
							ServiceUtil.stopService(blockchainNode.getServerIp(), blockchainNode.getSshPort(),
									commonInitBean.getPrivateKey(), blockchainNode.getServiceNeme());
							logger.info(String.format("Try to start Server %s with service %s.",
									blockchainNode.getServerIp(), blockchainNode.getServiceNeme()));
							ServiceUtil.startService(blockchainNode.getServerIp(), blockchainNode.getSshPort(),
									commonInitBean.getPrivateKey(), blockchainNode.getServiceNeme());
						});
			});

			commonInitBean.stopBlockchainRestarting(blockchain.getName());
		} else {
			logger.error(
					String.format("Blockchain %s has already be reastarting please be patint", blockchain.getName()));
		}
	}

	public void restartBlockchainNode(String rpcUrl) throws RuntimeException {
		if (!commonInitBean.doesNodeRestarting(rpcUrl)) {
			commonInitBean.startNodeRestarting(rpcUrl);
			Optional<BlockchainNode> bcn = blockchainNodeMicroService.findByrpcUrl(rpcUrl);
			if (bcn.isPresent()) {
				logger.info(String.format("Try to stop Server %s with service %s.", bcn.get().getServerIp(),
						bcn.get().getServiceNeme()));
				ServiceUtil.stopService(bcn.get().getServerIp(), bcn.get().getSshPort(), commonInitBean.getPrivateKey(),
						bcn.get().getServiceNeme());
				logger.info(String.format("Try to start Server %s with service %s.", bcn.get().getServerIp(),
						bcn.get().getServiceNeme()));
				ServiceUtil.startService(bcn.get().getServerIp(), bcn.get().getSshPort(),
						commonInitBean.getPrivateKey(), bcn.get().getServiceNeme());
			}
			commonInitBean.stopNodeRestarting(rpcUrl);
		} else {
			logger.error(String.format("Node 5s has bean already restarting skip ir", rpcUrl));
		}
	}

	@Transactional
	public SmartContract getContract(GeneralReq gr) throws RestActionError {
		if (gr == null)
			throw new RestActionError("Object is null");
		Optional<SmartContract> contract = Optional.empty();

		if (gr.getContractId() != null && gr.getContractId() > 0) {
			contract = smartContractMicroService.findById(gr.getContractId());
		}
		if (gr.getMarketMakingId() != null && gr.getMarketMakingId() > 0) {
			contract = marketMakingMicroService.findById(gr.getMarketMakingId()).map(MarketMaking::getSmartContract);
		}
		if (gr.getBlockchainId() != null && gr.getBlockchainId() > 0 && !Strings.isNullOrEmpty(gr.getContractAddress())) {
			Optional<Blockchain> blockchain = blockchainMicroService.findById(gr.getBlockchainId());
			if (blockchain.isPresent()) {
				contract = smartContractMicroService.findByBlockchainAndContractsAddress(blockchain.get(), gr.getContractAddress().trim());
			}
		}
		if (!Strings.isNullOrEmpty(gr.getBlockchain()) && !Strings.isNullOrEmpty(gr.getContractAddress())) {
			Optional<Blockchain> blockchain = blockchainMicroService.findByName(gr.getBlockchain().trim());
			if (blockchain.isPresent()) {
				contract = smartContractMicroService.findByBlockchainAndContractsAddress(blockchain.get(), gr.getContractAddress().trim());
			}
		}
		return contract.orElseThrow(() -> new RestActionError("Contract not found"));

	}

	@Transactional
	public MarketMaking getMarketMaking(GeneralReq gr) throws RestActionError {
		if (gr == null)
			throw new RestActionError("Object is null");
		Optional<MarketMaking> mm = Optional.empty();

		if (gr.getMarketMakingId() != null && gr.getMarketMakingId() > 0) {
			mm = marketMakingMicroService.findById(gr.getMarketMakingId());
		}
		if (gr.getContractId() != null && gr.getContractId() > 0) {
			var contractOptional = smartContractMicroService.findById(gr.getContractId());
			if (contractOptional.isPresent()) {
				mm = marketMakingMicroService.findBySmartContract(contractOptional.get());
			}
		}

		if (gr.getBlockchainId() != null && gr.getBlockchainId() > 0 && !Strings.isNullOrEmpty(gr.getContractAddress())) {
			Optional<Blockchain> blockchain = blockchainMicroService.findById(gr.getBlockchainId());
			if (blockchain.isPresent()) {
				var contractOptional = smartContractMicroService.findByBlockchainAndContractsAddress(blockchain.get(), gr.getContractAddress().trim());
				if (contractOptional.isPresent()) {
					mm = marketMakingMicroService.findBySmartContract(contractOptional.get());
				}
			}
		}
		if (!Strings.isNullOrEmpty(gr.getBlockchain()) && !Strings.isNullOrEmpty(gr.getContractAddress())) {
			Optional<Blockchain> blockchain = blockchainMicroService.findByName(gr.getBlockchain().trim());
			if (blockchain.isPresent()) {
				var contractOptional = smartContractMicroService.findByBlockchainAndContractsAddress(blockchain.get(), gr.getContractAddress().trim());
				if (contractOptional.isPresent()) {
					mm = marketMakingMicroService.findBySmartContract(contractOptional.get());
				}
			}
		}
		return mm.orElseThrow(() -> new RestActionError("MarketMaking not found"));

	}
}
