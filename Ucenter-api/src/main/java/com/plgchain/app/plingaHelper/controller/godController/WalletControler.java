/**
 *
 */
package com.plgchain.app.plingaHelper.controller.godController;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plgchain.app.plingaHelper.bean.BlockchainBean;
import com.plgchain.app.plingaHelper.controller.BaseController;
import com.plgchain.app.plingaHelper.util.MessageResult;

import lombok.RequiredArgsConstructor;

/**
 *
 */
@RestController
@RequestMapping("/api/v1/godaction")
@RequiredArgsConstructor
public class WalletControler extends BaseController implements Serializable {

	private static final long serialVersionUID = 1L;

	private final static Logger logger = LoggerFactory.getLogger(WalletControler.class);

	@Autowired
	private BlockchainBean blockchainBean;

	@RequestMapping("/wallet/fixWalletPrivatekeys")
	public MessageResult fixWalletPrivatekeys() {
		try {
			blockchainBean.fixWalletPrivatekeys();
			return success("All wallets has been fixed.");
		} catch (Exception e) {
			e.printStackTrace();
			return error(e.getMessage());
		}
	}

}
