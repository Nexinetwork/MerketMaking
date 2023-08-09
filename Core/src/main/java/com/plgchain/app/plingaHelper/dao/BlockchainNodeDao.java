package com.plgchain.app.plingaHelper.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.BlockchainNode;

public interface BlockchainNodeDao extends BaseLongDao<BlockchainNode> {
	public List<BlockchainNode> findByBlockchain(Blockchain blockchain);
	public long countByBlockchain(Blockchain blockchain);
	public List<BlockchainNode> findByBlockchainAndMustCheck(Blockchain blockchain,boolean mustCheck);

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM BlockchainNode e")
    public boolean anyExist();

	public List<BlockchainNode> deleteByBlockchain(Blockchain blockchain);

	public Long removeByBlockchain(Blockchain blockchain);

	public Optional<BlockchainNode> findByrpcUrl(String rpcUrl);

}
