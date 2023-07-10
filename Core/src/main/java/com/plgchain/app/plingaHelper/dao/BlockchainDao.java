package com.plgchain.app.plingaHelper.dao;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.entity.Blockchain;

public interface BlockchainDao extends BaseLongDao<Blockchain> {
	public Optional<Blockchain> findByName(String name);
	public boolean existsBlockchainByName(String name);
	public Blockchain findTopByOrderByLastCheckDesc();
	public Optional<Blockchain> findByMainCoin(String mainCoin);
	public boolean existsBlockchainByMainCoin(String mainCoin);
	public Optional<Blockchain> findByChainId(BigInteger chainId);
	public boolean existsBlockchainByChainId(BigInteger chainId);
	public List<Blockchain> findByIsEvm(boolean isEvm);
	public Optional<Blockchain> findByCoingeckoId(String coingeckoId);
	public boolean existsBlockchainByCoingeckoId(String coingeckoId);

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Blockchain e")
    public boolean anyExist();

}
