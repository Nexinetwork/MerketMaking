package com.plgchain.app.plingaHelper.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plgchain.app.plingaHelper.constant.WalletType;
import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;
import com.plgchain.app.plingaHelper.entity.marketMaking.MarketMakingWallet;

public interface MarketMakingWalletDao extends BaseLongDao<MarketMakingWallet> {

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM MarketMakingWallet e")
	public boolean anyExist();
	public List<MarketMakingWallet> findByContract(SmartContract contract);
    public Page<MarketMakingWallet> findByContract(SmartContract contract, Pageable pageable);

	public long countByContract(SmartContract contract);
	public boolean existsByContract(SmartContract contract);
	public List<MarketMakingWallet> findByContractAndWalletType(SmartContract contract, WalletType walletType);
	public long countByContractAndWalletType(SmartContract contract, WalletType walletType);
	public boolean existsByContractAndWalletType(SmartContract contract, WalletType walletType);
	public List<MarketMakingWallet> findByPublicKey(String publicKey);
	public List<MarketMakingWallet> findByContractOrderByMmWalletIdDesc(SmartContract contract);

	@Query("SELECT wallet FROM MarketMakingWallet wallet WHERE wallet.contract = :contract ORDER BY FUNCTION('RANDOM') LIMIT :count")
	public List<MarketMakingWallet> findNByContractOrderByRandom(@Param("contract") SmartContract contract, @Param("count") int count);

	@Query("SELECT wallet FROM MarketMakingWallet wallet WHERE wallet.contract = :contract ORDER BY RANDOM()")
    public Page<MarketMakingWallet> findRandomByContract(@Param("contract") SmartContract contract, Pageable pageable);

	@Query("SELECT wallet FROM MarketMakingWallet wallet ORDER BY FUNCTION('RANDOM') LIMIT :count")
	public List<MarketMakingWallet> findNOrderByRandom(@Param("count") int count);

	@Query(nativeQuery=true, value="SELECT \"mmWalletId\",PGP_SYM_DECRYPT( \"privateKey\", '!@MYLoveTeted2023secretLOGINILoveYouTedTed@!' ) as \"privateKey\",PGP_SYM_DECRYPT( \"privateKeyHex\", '!@MYLoveTeted2023secretLOGINILoveYouTedTed@!' ) as \"privateKeyHex\",\"publicKey\",balance,\"mainCoinBalance\",\"contractId\",\"coinId\",\"blockchainId\",\"contractAddress\",\"walletType\",\"creationDate\",\"lastModifiedDate\" FROM \"schMarketMaking\".\"tblMMWallet\" WHERE \"contractId\"= :contractId ORDER BY RANDOM() LIMIT :count")
	public List<MarketMakingWallet> findNWalletsRandomByContractIdNative(@Param("contractId") long contractId,@Param("count") int count);

	@Query(nativeQuery=true, value="SELECT \"mmWalletId\",PGP_SYM_DECRYPT( \"privateKey\", '!@MYLoveTeted2023secretLOGINILoveYouTedTed@!' ) as \"privateKey\",PGP_SYM_DECRYPT( \"privateKeyHex\", '!@MYLoveTeted2023secretLOGINILoveYouTedTed@!' ) as \"privateKeyHex\",\"publicKey\",balance,\"mainCoinBalance\",\"contractId\",\"coinId\",\"blockchainId\",\"contractAddress\",\"walletType\",\"creationDate\",\"lastModifiedDate\" FROM \"schMarketMaking\".\"tblMMWallet\" WHERE \"contractId\"= :contractId AND \"walletType\" = :walletType ORDER BY RANDOM() LIMIT :count")
	public List<MarketMakingWallet> findNWalletsRandomByContractIdAndWalletTypeNative(@Param("contractId") long contractId,@Param("walletType") int walletType,@Param("count") int count);

	@Query(nativeQuery=true, value="SELECT \"mmWalletId\",PGP_SYM_DECRYPT( \"privateKey\", '!@MYLoveTeted2023secretLOGINILoveYouTedTed@!' ) as \"privateKey\",PGP_SYM_DECRYPT( \"privateKeyHex\", '!@MYLoveTeted2023secretLOGINILoveYouTedTed@!' ) as \"privateKeyHex\",\"publicKey\",balance,\"mainCoinBalance\",\"contractId\",\"coinId\",\"blockchainId\",\"contractAddress\",\"walletType\",\"creationDate\",\"lastModifiedDate\" FROM \"schMarketMaking\".\"tblMMWallet\" WHERE \"contractId\"= :contractId")
	public List<MarketMakingWallet> findAllWalletsByContractIdNative(@Param("contractId") long contractId);

	@Query(nativeQuery=true, value="SELECT \"mmWalletId\",PGP_SYM_DECRYPT( \"privateKey\", '!@MYLoveTeted2023secretLOGINILoveYouTedTed@!' ) as \"privateKey\",PGP_SYM_DECRYPT( \"privateKeyHex\", '!@MYLoveTeted2023secretLOGINILoveYouTedTed@!' ) as \"privateKeyHex\",\"publicKey\",balance,\"mainCoinBalance\",\"contractId\",\"coinId\",\"blockchainId\",\"contractAddress\",\"walletType\",\"creationDate\",\"lastModifiedDate\" FROM \"schMarketMaking\".\"tblMMWallet\" WHERE \"contractId\"= :contractId AND \"walletType\" = :walletType")
	public List<MarketMakingWallet> findAllWalletsByContractIdAndWalletTypeNative(@Param("contractId") long contractId,@Param("walletType") int walletType);

	@Query(nativeQuery=true, value="SELECT \"mmWalletId\",PGP_SYM_DECRYPT( \"privateKey\", '!@MYLoveTeted2023secretLOGINILoveYouTedTed@!' ) as \"privateKey\",PGP_SYM_DECRYPT( \"privateKeyHex\", '!@MYLoveTeted2023secretLOGINILoveYouTedTed@!' ) as \"privateKeyHex\",\"publicKey\",balance,\"mainCoinBalance\",\"contractId\",\"coinId\",\"blockchainId\",\"contractAddress\",\"walletType\",\"creationDate\",\"lastModifiedDate\" FROM \"schMarketMaking\".\"tblMMWallet\" TABLESAMPLE WHERE \"contractId\"= :contractId system_rows(:count)")
	public List<MarketMakingWallet> findNWalletsRandomByContractIdNativeTABLESAMPLE(@Param("contractId") long contractId,@Param("count") int count);

	@Query(nativeQuery=true, value="SELECT \"mmWalletId\",PGP_SYM_DECRYPT( \"privateKey\", '!@MYLoveTeted2023secretLOGINILoveYouTedTed@!' ) as \"privateKey\",PGP_SYM_DECRYPT( \"privateKeyHex\", '!@MYLoveTeted2023secretLOGINILoveYouTedTed@!' ) as \"privateKeyHex\",\"publicKey\",balance,\"mainCoinBalance\",\"contractId\",\"coinId\",\"blockchainId\",\"contractAddress\",\"walletType\",\"creationDate\",\"lastModifiedDate\" FROM \"schMarketMaking\".\"tblMMWallet\" TABLESAMPLE WHERE \"contractId\"= :contractId  AND \"walletType\" = :walletType system_rows(:count)")
	public List<MarketMakingWallet> findNWalletsRandomByContractIdAndWalletTypeNativeTABLESAMPLE(@Param("contractId") long contractId,@Param("walletType") int walletType,@Param("count") int count);

}
