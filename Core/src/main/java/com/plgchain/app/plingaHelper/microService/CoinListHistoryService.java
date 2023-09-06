/**
 *
 */
package com.plgchain.app.plingaHelper.microService;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;

import com.plgchain.app.plingaHelper.dao.CoinListHistoryDao;
import com.plgchain.app.plingaHelper.dto.CoinListDto;
import com.plgchain.app.plingaHelper.entity.CoinListHistory;
import com.plgchain.app.plingaHelper.entity.coingecko.CoinList;
import com.plgchain.app.plingaHelper.microService.Base.BaseService;

import jakarta.inject.Inject;

/**
 *
 */
@Service
public class CoinListHistoryService extends BaseService<CoinListHistory> implements Serializable {

	private static final long serialVersionUID = 4346595704862253248L;

	@Inject
	private CoinListHistoryDao coinListHistoryDao;

	public CoinListHistory findFirst() {
		return coinListHistoryDao.findFirstBy();
	}

	public boolean isEmptyDocument() {
	    return coinListHistoryDao.count() == 0;
	}

	public CoinListHistory save(CoinList coinList) {
		CoinListHistory clh = coinListHistoryDao.findFirstBy();
		clh.getCoinListList().add(coinList.getAsDto());
		return coinListHistoryDao.save(clh);

	}

	public CoinListHistory save(CoinListDto coinListDto) {
		CoinListHistory clh = coinListHistoryDao.findFirstBy();
		clh.getCoinListList().add(coinListDto);
		return coinListHistoryDao.save(clh);

	}

	public boolean anyExist() {
		return coinListHistoryDao.anyExist();
	}

	public void delete(CoinListHistory object) {
		coinListHistoryDao.delete(object);
	}

	public void deleteAll() {
		coinListHistoryDao.deleteAll();
	}

	public List<CoinListHistory> saveAll(List<CoinListHistory> oList) {
		return coinListHistoryDao.saveAll(oList);
	}

}
