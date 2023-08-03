/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.dao.MarketMakingWalletDao;
import com.plgchain.app.plingaHelper.dto.MarketMakingWalletDto;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMakingWallet;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

import jakarta.inject.Inject;

/**
 *
 */
@Service
public class MarketMakingWalletService extends BaseService<MarketMakingWallet> implements Serializable {

	private static final long serialVersionUID = -1912998788562981610L;

	@Inject
	private MarketMakingWalletDao marketMakingWalletDao;

	public Optional<MarketMakingWallet> findById(Long id) {
		return marketMakingWalletDao.findById(id);
	}

	public MarketMakingWallet save(MarketMakingWallet mmw) {
		return marketMakingWalletDao.save(mmw);
	}

	public MarketMakingWallet saveAndFlush(MarketMakingWallet mmw) {
		return marketMakingWalletDao.saveAndFlush(mmw);
	}

	public List<MarketMakingWallet> findAll() {
		return marketMakingWalletDao.findAll();
	}

	public boolean anyExist() {
		return marketMakingWalletDao.anyExist();
	}

	public void delete(MarketMakingWallet object) {
		marketMakingWalletDao.delete(object);
	}

	public void deleteAll() {
		marketMakingWalletDao.deleteAll();
	}

	public List<MarketMakingWallet> saveAll(List<MarketMakingWallet> oList) {
		return marketMakingWalletDao.saveAll(oList);
	}

	public List<MarketMakingWallet> findByContractOrderByMmWalletIdDesc(SmartContract contract) {
		return marketMakingWalletDao.findByContractOrderByMmWalletIdDesc(contract);
	}

	public List<MarketMakingWallet> findNByContractOrderByRandom(SmartContract contract,int count) {
		return marketMakingWalletDao.findNByContractOrderByRandom(contract,count);
	}

	public Page<MarketMakingWallet> findRandomByContract(SmartContract contract, Pageable pageable) {
		return marketMakingWalletDao.findRandomByContract(contract, pageable);
	}

	public List<MarketMakingWallet> findNOrderByRandom(int count) {
		return marketMakingWalletDao.findNOrderByRandom(count);
	}

	public List<MarketMakingWallet> batchSaveAll(List<MarketMakingWallet> oList, int count) {
        return IntStream.range(0, oList.size())
                .boxed()
                .collect(Collectors.groupingBy(index -> index / count))
                .values()
                .stream()
                .map(batchIndexes -> batchIndexes.stream()
                        .map(oList::get)
                        .collect(Collectors.toList()))
                .flatMap(batchList -> saveAll(batchList).stream())
                .collect(Collectors.toList());
    }

	public List<MarketMakingWallet> findByContract(SmartContract contract) {
		return marketMakingWalletDao.findByContract(contract);
	}

	public Page<MarketMakingWallet> findByContractWithPaging(SmartContract contract, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return marketMakingWalletDao.findByContract(contract, pageable);
    }

	public Page<MarketMakingWallet> findByContractWithPaging(SmartContract contract, Pageable pageable) {
        return marketMakingWalletDao.findByContract(contract, pageable);
    }

	public List<MarketMakingWallet> findByPublicKey(String publicKey) {
		return marketMakingWalletDao.findByPublicKey(publicKey);
	}

	public long countByContract(SmartContract contract) {
		return marketMakingWalletDao.countByContract(contract);
	}

	public boolean existsByContract(SmartContract contract) {
		return marketMakingWalletDao.existsByContract(contract);
	}

	public List<MarketMakingWallet> findByContractAndWalletType(SmartContract contract, WalletType walletType) {
		return marketMakingWalletDao.findByContractAndWalletType(contract, walletType);
	}

	public long countByContractAndWalletType(SmartContract contract, WalletType walletType) {
		return marketMakingWalletDao.countByContractAndWalletType(contract, walletType);
	}

	public boolean existsByContractAndWalletType(SmartContract contract, WalletType walletType) {
		return marketMakingWalletDao.existsByContractAndWalletType(contract, walletType);
	}

	public List<MarketMakingWalletDto> findNWalletsRandomByContractIdNative(long contractId,int count) {
		return marketMakingWalletDao.findNWalletsRandomByContractIdNative(contractId, count);
	}

	public List<MarketMakingWalletDto> findNWalletsRandomByContractIdNative(SmartContract contract,int count) {
		return marketMakingWalletDao.findNWalletsRandomByContractIdNative(contract.getContractId(), count);
	}

}
