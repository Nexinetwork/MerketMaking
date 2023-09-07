package com.plgchain.app.plingaHelper.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.plgchain.app.plingaHelper.entity.MMWallet;

public interface MMWalletDao extends MongoRepository<MMWallet,Long> {
	public List<MMWallet> findByMarketMakingId(long marketMakingId);
	public List<MMWallet> findByContractId(long contractId);
	public Optional<MMWallet> findByMarketMakingIdAndChunk(long marketMakingId,long chunk);
	public Optional<MMWallet> findByContractIdAndChunk(long contractId,long chunk);
	public Optional<MMWallet> findTopByMarketMakingIdOrderByChunkDesc(long marketMakingId);
	public Optional<MMWallet> findTopByContractIdOrderByChunkDesc(long contractId);
	public void deleteByMarketMakingId(long marketMakingId);
	public void deleteByContractId(long contractId);
}
