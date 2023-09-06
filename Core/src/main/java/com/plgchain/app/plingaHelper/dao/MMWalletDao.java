package com.plgchain.app.plingaHelper.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.plgchain.app.plingaHelper.entity.MMWallet;

public interface MMWalletDao extends MongoRepository<MMWallet,Long> {
	public Optional<MMWallet> findByContractId(long contractId);
	public Optional<MMWallet> findByContractIdAndChunk(long contractId,long chunk);
	public Optional<MMWallet> findTopByContractIdOrderByChunkDesc(long contractId);
}
