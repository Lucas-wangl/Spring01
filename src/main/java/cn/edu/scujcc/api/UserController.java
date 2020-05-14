package cn.edu.scujcc.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.scujcc.UserExistException;
import cn.edu.scujcc.model.Result;
import cn.edu.scujcc.model.User;
import cn.edu.scujcc.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	
	@Autowired
	private CacheManager cacheManager;
	
	@Autowired
	private UserService service;
	@PostMapping("/register")
	public Result register(@RequestBody User u) {
		Result result = new Result();
		logger.debug("����ע���û����û����ݣ�"+u);
		User saved = null;
		try {
			saved = service.createUser(u);
			result.setStatus(Result.OK);
			result.setMessage("ע��ɹ�");
			result.setDate(saved);
		}catch (UserExistException e) {
			logger.error("�û����Ѿ����ڣ�",e);
			result.setStatus(Result.DUPLICATED);
			result.setMessage("�û����Ѿ����ڣ�");
		}
		return result;
	}
	
	@GetMapping("/login/{username}/{password}")
	public Result login(@PathVariable String username, @PathVariable String password) {
		Result result = new Result();
		boolean status = service.checkUser(username, password);
		if (status) {//��¼�ɹ�
			result.setStatus(Result.OK);
			result.setMessage("��¼�ɹ�");
			//���û����뻺��
			Cache cache = cacheManager.getCache(User.CACHE_NAME);
			cache.put("current_user",username);
		}else {
			result.setStatus(Result.ERROR);
			result.setMessage("��¼ʧ��");
		}
		return result;
	}

}
