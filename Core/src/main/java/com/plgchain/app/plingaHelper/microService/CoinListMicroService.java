package com.plgchain.app.plingaHelper.microService;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dao.CoinListDao;
import com.plgchain.app.plingaHelper.entity.coingecko.CoinList;
import com.plgchain.app.plingaHelper.microService.Base.BaseMicroService;

import jakarta.inject.Inject;

@Service
public class CoinListMicroService extends BaseMicroService<CoinList> implements Serializable {

	private static final long serialVersionUID = -6413325789707324383L;

	@Inject
	private CoinListDao coinListDao;

	@Inject
	private SequenceGeneratorMicroService sequenceGeneratorMicroService;

	public CoinList findFirst() {
		return coinListDao.findFirstBy();
	}

	public boolean isEmptyDocument() {
	    return coinListDao.count() == 0;
	}

	public CoinList save(CoinList cl) {
		if (cl.getId() <= 0)
			cl.setId(sequenceGeneratorMicroService.generateSequence(CoinList.SEQUENCE_NAME));
		return coinListDao.save(cl);
	}

	public boolean anyExist() {
		return coinListDao.anyExist();
	}

	public void delete(CoinList object) {
		coinListDao.delete(object);
	}

	public void deleteAll() {
		coinListDao.deleteAll();
	}

	public List<CoinList> saveAll(List<CoinList> oList) {
		return coinListDao.saveAll(oList);
	}



}
