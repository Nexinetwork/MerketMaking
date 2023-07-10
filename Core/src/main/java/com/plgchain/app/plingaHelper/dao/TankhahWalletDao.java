package com.plgchain.app.plingaHelper.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.plgchain.app.plingaHelper.dao.base.BaseLongDao;
import com.plgchain.app.plingaHelper.entity.TankhahWallet;
import com.plgchain.app.plingaHelper.entity.coingecko.SmartContract;

public interface TankhahWalletDao extends BaseLongDao<TankhahWallet> {
	public boolean existsTankhahWalletByContract(SmartContract contract);
	public List<TankhahWallet> findByContract(SmartContract contract);

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM TankhahWallet e")
    public boolean anyExist();

}
