/**
 *
 */
package com.plgchain.app.plingaHelper.microService;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dao.BlockchainDao;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.microService.Base.BaseMicroService;

/**
 *
 */
@Service
public class BlockchainMicroService extends BaseMicroService<Blockchain> implements Serializable {

	private static final long serialVersionUID = -8641271050704411082L;

	@Autowired
	private BlockchainDao blockchainDao;

	public Optional<Blockchain> findById(Long id) {
		return blockchainDao.findById(id);
	}

	public boolean existById(Long id) {
		return blockchainDao.existsById(id);
	}

	public Optional<Blockchain> findByName(String name) {
		return blockchainDao.findByName(name);
	}

	public boolean existsBlockchainByName(String name) {
		return blockchainDao.existsBlockchainByName(name);
	}

	public Optional<Blockchain> findByMainCoin(String name) {
		return blockchainDao.findByMainCoin(name);
	}

	public boolean existsBlockchainByMainCoin(String name) {
		return blockchainDao.existsBlockchainByMainCoin(name);
	}

	public Blockchain save(Blockchain blockchain) {
		return blockchainDao.save(blockchain);
	}

	public Blockchain saveAndFlush(Blockchain blockchain) {
		return blockchainDao.saveAndFlush(blockchain);
	}

	public List<Blockchain> findAll() {
		return blockchainDao.findAll();
	}

	public Blockchain findTopByOrderByLastCheckDesc() {
		return blockchainDao.findTopByOrderByLastCheckDesc();
	}

	public Optional<Blockchain> findByChainId(BigInteger chainId) {
		return blockchainDao.findByChainId(chainId);
	}

	public boolean existsBlockchainByChainId(BigInteger chainId) {
		return blockchainDao.existsBlockchainByChainId(chainId);
	}

	public List<Blockchain> findByIsEvm(boolean isEvm) {
		return blockchainDao.findByIsEvm(isEvm);
	}

	public Optional<Blockchain> findByCoingeckoId(String coingeckoId) {
		return blockchainDao.findByCoingeckoId(coingeckoId);
	}

	public boolean existsBlockchainByCoingeckoId(String coingeckoId) {
		return blockchainDao.existsBlockchainByCoingeckoId(coingeckoId);
	}

	public boolean anyExist() {
		return blockchainDao.anyExist();
	}

	public void delete(Blockchain object) {
		blockchainDao.delete(object);
	}

	public void deleteAll() {
		blockchainDao.deleteAll();
	}

	public List<Blockchain> saveAll(List<Blockchain> oList) {
		return blockchainDao.saveAll(oList);
	}

}
