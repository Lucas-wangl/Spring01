package cn.edu.scujcc.dao;

import java.util.List;


import org.springframework.data.mongodb.repository.MongoRepository;


import cn.edu.scujcc.model.Channel;


public interface ChannelRepository extends MongoRepository<Channel,String>{
	

	public List<Channel> findByQuality(String quality);
	public Channel findFirstByQuality(String quality);
	
	public List<Channel> findByTitle(String title);
	/**
	 * �ҳ�û�����۵�Ƶ��
	 * @return
	 */
	public List<Channel> findByCommentsNull();
	

}
