package com.plgchain.app.plingaHelper.dao;

import java.util.List;
import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.BlockchainNode;

public interface BlockchainNodeDao extends BaseLongDao<BlockchainNode> {
	public List<BlockchainNode> findByBlockchain(Blockchain blockchain);
	public List<BlockchainNode> findByBlockchainAndMustCheck(Blockchain blockchain,boolean mustCheck);

}
