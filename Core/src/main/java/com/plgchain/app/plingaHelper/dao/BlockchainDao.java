package com.plgchain.app.plingaHelper.dao;

import java.util.Optional;

import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.entity.Blockchain;

public interface BlockchainDao extends BaseLongDao<Blockchain> {
	public Optional<Blockchain> findByName(String name);
	public boolean existsBlockchainByName(String name);
	public Blockchain findTopByOrderByLastCheckDesc();
	public Optional<Blockchain> findByMainCoin(String mainCoin);
	public boolean existsBlockchainByMainCoin(String mainCoin);

}
