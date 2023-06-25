package com.plgchain.app.plingaHelper.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dao.CoinListDao;
import com.plgchain.app.plingaHelper.entity.coingecko.CoinList;
import com.plgchain.app.plingaHelper.service.Base.BaseService;

import jakarta.inject.Inject;

@Service
public class CoinListService extends BaseService<CoinList> implements Serializable {

	private static final long serialVersionUID = -6413325789707324383L;

	@Inject
	private CoinListDao coinListDao;

	@Inject
	private SequenceGeneratorService sequenceGeneratorService;

	public CoinList findFirst() {
		return coinListDao.findFirstBy();
	}

	public boolean isEmptyDocument() {
	    return coinListDao.count() == 0;
	}

	public CoinList saveCoinList(CoinList cl) {
		if (cl.getId() <= 0)
			cl.setId(sequenceGeneratorService.generateSequence(CoinList.SEQUENCE_NAME));
		return coinListDao.save(cl);
	}



}
