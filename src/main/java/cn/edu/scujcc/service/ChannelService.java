package cn.edu.scujcc.service;

import java.util.Optional;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import cn.edu.scujcc.dao.ChannelRepository;
import cn.edu.scujcc.model.Channel;
import cn.edu.scujcc.model.Comment;

@Service
public class ChannelService {
	public static final Logger logger = LoggerFactory.getLogger(ChannelService.class);
	@Autowired
	private ChannelRepository repo;
//    private List<Channel> channels;
//	
//    public ChannelService() {
//		channels=new ArrayList<>();
//		for (int i=0;i<10;i++) {
//			Channel c=new Channel();
////			c.setId(i+1);
//			c.setTitle("中央"+(i+1)+"台");
//			c.setUrl("http://www.cctv.com");
//			channels.add(c);
//		}
	
    /**
     * 获取所有频道数据
     * @return 频道List
     */
	@Cacheable(cacheNames="channels",key="'all_cahnnels'")
	public List<Channel> getAllChannels(){
		logger.debug("从数据库中读取所有频道信息...");
		return repo.findAll();
		
	}
	/***
	 * 获取一个频道数据
	 * @param channelId 频道编号
	 * @return 频道对象，若未找到则返回null
	 */
	@Cacheable(cacheNames="channels",key="#channelId")
	public Channel getChannel(String channelId) {
		logger.debug("从数据库中读取频道:"+channelId);
		Optional<Channel>result = repo.findById(channelId);
		
		if(result.isPresent()) {
			return result.get();
		}else {
			return null;
		}
	}
		
//		Channel result=null;
//		//循环查找指定的频道
//		for (Channel c: channels) {
////			if(c.getId()==channelId) {
////				result=c;
////				break;
////			}
//		}
//		return result;
//	}
	/**
	 * 删除指定的频道
	 * @param channelId 待删除的频道编号
	 * @return 若删除成功返回true，否则返回false
	 */ 
	public boolean deteleChannel(String channelId) {
		boolean result=true;
		repo.deleteById(channelId);
		return result;
	}
		
//		Channel c=getChannel(channelId);
//		if(c !=null) {
//			channels.remove(c);
//			result=true;
//		}
//		return result;
//	}
	/**
	 * 保存频道
	 * @param c 待保存的频道对象（没有id值）
	 * @return 保存后的频道（有id值）
	 */
	@CachePut(cacheNames="channels",key="#result.id")
	public Channel createChannel(Channel c) {
//		找到目前最大的id，并增加1作为新频道的id
//		int newId=channels.get(channels.size()-1).getId()+1;
//		c.setId(newId);
//		channels.add(c);
//		return c;
		return repo.save(c);
		}
	
	/**
	 * 更新指定的频道信息
	 * @param c 新的频道信息，用于更新已存在的同一频道
	 * @return 更新后的频道信息
	 */
	@CacheEvict(cacheNames="channels",key="'all_cahnnels'")

	public Channel updateChannel(Channel c) {
		Channel saved = getChannel(c.getId());
		if (c.getTitle() !=null) {
		saved.setTitle(c.getTitle());
		}
		if (c.getQuality() !=null) {
			saved.setQuality(c.getQuality());
			}
		if (c.getUrl() !=null) {
			saved.setUrl(c.getUrl());
			}
		//把新评论追加到老评论后面
		if (c.getComments() !=null) {
			if(saved.getComments() != null) {
			saved.getComments().addAll(c.getComments());
			}else {//用新评论代替老评论
				saved.setComments(c.getComments());
			}
		}
		if (c.getCover() !=null) {
			saved.setCover(c.getCover());
		}
		
		return repo.save(saved);//保存更新后的实体对象
	}
//		Channel toUpdate=getChannel(c.getId());
//		if (toUpdate !=null) {
//			toUpdate.setTitle(c.getTitle());
//			toUpdate.setQuality(c.getQuality());
//			toUpdate.setUrl(c.getUrl());
//		}
//		return toUpdate;
		
	
	
	public List<Channel> searchByQuality(String quality){
		return repo.findByQuality(quality);
	}
	public List<Channel> searchByTitle(String title){
		return repo.findByTitle(title);
	}
	/**
	 * 获取冷门频道
	 * @return
	 */
	public List<Channel> findColdChannels(){
		return repo.findByCommentsNull();
	}
	public List<Channel> findChannelsPage(int page){
		Page<Channel> p=repo.findAll(PageRequest.of(page, 3));
		return p.toList();
	}
	/**
	 * 像指定频道添加一条评论
	 * @param channelId 目标频道的编号
	 * @param comment 即将添加的频道
	 */
	public Channel addComment(String channelId, Comment comment) {
		Channel result = null;
		Channel saved = getChannel(channelId);
		if (null != saved) {//数据库中有该频道
			saved.addComment(comment);
			result = repo.save(saved);
		}
		return result;
	}
	/**
	 * 获取目标频道的热门评论
	 * @param channelId 目标频道的编号
	 * @return 评论列表
	 */
	public List<Comment>hotComments(String channelId){
		List<Comment> result = null;
		Channel saved = getChannel(channelId);
		if (saved != null) {
		result = saved.getComments();
		result.sort(new Comparator<Comment>() {
			@Override
			public int compare(Comment o1,Comment o2) {
		     //若o1比o2小，则返回负数；若o1比o2大，则返回正数；若o1等于o2，则还回0
				int re=0;
				if (o1.getStar()<o2.getStar()) {
				re=1;	
				}else if (o1.getStar()>o2.getStar()) {
					re=-1;
				}
				return re;
			}
		});
		if (result.size()>3) {
			result = result.subList(0,3);
		}
		logger.debug("热门评论有"+result.size()+"条...");
		logger.debug(result.toString());
		}else {
			logger.warn("指定的频道不存在，id="+channelId);
		}
		return result;
	}
}
	
	


	
	


