package com.plgchain.app.plingaHelper.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.entity.Blockchain;
import com.plgchain.app.plingaHelper.entity.coingecko.Coin;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;

public interface SmartContractDao extends BaseLongDao<SmartContract> {
	public List<SmartContract> findByContractsAddress(String contractsAddress);
	public boolean existsSmartContractByContractsAddress(String contractsAddress);
	public Optional<SmartContract> findByBlockchainAndContractsAddress(Blockchain blockchain,String contractsAddress);
	public boolean existsSmartContractByBlockchainAndContractsAddress(Blockchain blockchain,String contractsAddress);
	public List<SmartContract> findByCoinAndContractsAddress(Coin coin,String contractsAddress);
	public boolean existsSmartContractByCoinAndContractsAddress(Coin coin,String contractsAddress);
	public Optional<SmartContract> findByBlockchainAndCoinAndContractsAddress(Blockchain blockchain,Coin coin,String contractsAddress);
	public boolean existsSmartContractByBlockchainAndCoinAndContractsAddress(Blockchain blockchain,Coin coin,String contractsAddress);
	public Optional<SmartContract> findByBlockchainAndCoin(Blockchain blockchain,Coin coin);
	public boolean existsSmartContractByBlockchainAndCoin(Blockchain blockchain,Coin coin);
	public List<SmartContract> findByMustAdd(boolean mustAdd);

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM SmartContract e")
    public boolean anyExist();

}
