package cn.edu.scujcc.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.edu.scujcc.dao.UserRepository;
import cn.edu.scujcc.model.User;


@Service
public class UserService {
	@Autowired
	private UserRepository repo;
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	/**
	 * �û�ע��
	 * @param user
	 * @return
	 */
	public User createUser(User user) {
		logger.debug("�û�ע�᣺"+user);
		User result = null;
		//TODO 1.����ǰ���û��������
		//TODO 2.����û����Ƿ��Ѿ����ڣ��������������ע��
		result = repo.save(user);
		return result;
	}
	/**
	 * ����û����������Ƿ�ƥ��
	 * @param username �û���¼��
	 * @param password �û�����
	 * @return ���������ȷ�򻹻�true�����򻹻�false
	 */
	public boolean checkUser(String username, String password) {
		boolean result = false;
		User u = repo.findOneByUsernameAndPassword(username, password);
		logger.debug("���ݿ��е��û���Ϣ�ǣ�"+u);
		if (null != u) {
			result = true;
	}
		return result;
}
		
}
