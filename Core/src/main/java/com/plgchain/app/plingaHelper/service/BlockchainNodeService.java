/**
 *
 */
package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dao.BlockchainNodeDao;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.BlockchainNode;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

import jakarta.transaction.Transactional;

/**
 *
 */
@Service
public class BlockchainNodeService extends BaseService<BlockchainNode> implements Serializable {

	private static final long serialVersionUID = -8641271050704411082L;

	@Autowired
	private BlockchainNodeDao blockchainNodeDao;

	public Optional<BlockchainNode> findById(Long id) {
		return blockchainNodeDao.findById(id);
	}

	public List<BlockchainNode> findByBlockchain(Blockchain blockchain) {
		return blockchainNodeDao.findByBlockchain(blockchain);
	}

	public List<BlockchainNode> findByBlockchainAndMustCheck(Blockchain blockchain,boolean mustCheck) {
		return blockchainNodeDao.findByBlockchainAndMustCheck(blockchain,mustCheck);
	}


	public BlockchainNode save(BlockchainNode blockchainNode) {
		return blockchainNodeDao.save(blockchainNode);
	}

	public BlockchainNode saveAndFlush(BlockchainNode blockchainNode) {
		return blockchainNodeDao.saveAndFlush(blockchainNode);
	}

	public List<BlockchainNode> findAll() {
		return blockchainNodeDao.findAll();
	}

	public long countByBlockchain(Blockchain blockchain) {
		return blockchainNodeDao.countByBlockchain(blockchain);
	}

	public boolean anyExist() {
		return blockchainNodeDao.anyExist();
	}

	public void delete(BlockchainNode object) {
		blockchainNodeDao.delete(object);
	}

	public void deleteAll() {
		blockchainNodeDao.deleteAll();
	}

	public List<BlockchainNode> saveAll(List<BlockchainNode> oList) {
		return blockchainNodeDao.saveAll(oList);
	}

	@Transactional
	public List<BlockchainNode> deleteByBlockchain(Blockchain blockchain) {
		return blockchainNodeDao.deleteByBlockchain(blockchain);
	}

	@Transactional
	public Long removeByBlockchain(Blockchain blockchain) {
		return blockchainNodeDao.removeByBlockchain(blockchain);
	}

	public Optional<BlockchainNode> findByrpcUrl(String rpcUrl) {
		return blockchainNodeDao.findByrpcUrl(rpcUrl);
	}

	public List<BlockchainNode> findByEnabledAndMustCheck(boolean enabled,boolean mustCheck) {
		return blockchainNodeDao.findByEnabledAndMustCheck(enabled,mustCheck);
	}

}
