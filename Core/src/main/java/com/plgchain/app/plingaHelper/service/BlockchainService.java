/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dao.BlockchainDao;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

/**
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class BlockchainService extends BaseService implements Serializable {

	private static final long serialVersionUID = -8641271050704411082L;

	@Autowired
	private BlockchainDao blockchainDao;

	public Blockchain findById(Long id) {
		return blockchainDao.findById(id).get();
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

}
