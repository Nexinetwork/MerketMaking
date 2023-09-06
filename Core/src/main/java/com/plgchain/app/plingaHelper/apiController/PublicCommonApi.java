package com.plgchain.app.plingaHelper.apiController;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import com.plgchain.app.plingaHelper.constant.UserStatus;
import com.plgchain.app.plingaHelper.controller.BaseController;
import com.plgchain.app.plingaHelper.entity.User;
import com.plgchain.app.plingaHelper.microService.UserService;
import com.plgchain.app.plingaHelper.util.MessageResult;

//@CrossOrigin(origins = "*", maxAge = 3600)
//@RestController
//@RequestMapping("/api/v1/api/common")
public class PublicCommonApi extends BaseController implements Serializable {

	private static final long serialVersionUID = -1421027553562847647L;

	private final PasswordEncoder passwordEncoder;

	@Autowired
	private UserService userService;

	public PublicCommonApi(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

	@RequestMapping(value = "/initUser")
	public MessageResult createNewCoin() {
		User user = new User();
		user.setEmailAddress("info@Plinga.technology");
		user.setFirstname("Peter");
		user.setLastname("Richardson");
		user.setUserStatus(UserStatus.Active);
		user.setPassword(passwordEncoder.encode("MYLoveArash2023plgchainLOGIN"));
		userService.save(user);
		return success("Admin User Created.");
	}

}
