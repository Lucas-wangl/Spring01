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
//			c.setTitle("����"+(i+1)+"̨");
//			c.setUrl("http://www.cctv.com");
//			channels.add(c);
//		}
	
    /**
     * ��ȡ����Ƶ������
     * @return Ƶ��List
     */
	@Cacheable(cacheNames="channels",key="'all_cahnnels'")
	public List<Channel> getAllChannels(){
		logger.debug("�����ݿ��ж�ȡ����Ƶ����Ϣ...");
		return repo.findAll();
		
	}
	/***
	 * ��ȡһ��Ƶ������
	 * @param channelId Ƶ�����
	 * @return Ƶ��������δ�ҵ��򷵻�null
	 */
	@Cacheable(cacheNames="channels",key="#channelId")
	public Channel getChannel(String channelId) {
		logger.debug("�����ݿ��ж�ȡƵ��:"+channelId);
		Optional<Channel>result = repo.findById(channelId);
		
		if(result.isPresent()) {
			return result.get();
		}else {
			return null;
		}
	}
		
//		Channel result=null;
//		//ѭ������ָ����Ƶ��
//		for (Channel c: channels) {
////			if(c.getId()==channelId) {
////				result=c;
////				break;
////			}
//		}
//		return result;
//	}
	/**
	 * ɾ��ָ����Ƶ��
	 * @param channelId ��ɾ����Ƶ�����
	 * @return ��ɾ���ɹ�����true�����򷵻�false
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
	 * ����Ƶ��
	 * @param c �������Ƶ������û��idֵ��
	 * @return ������Ƶ������idֵ��
	 */
	@CachePut(cacheNames="channels",key="#result.id")
	public Channel createChannel(Channel c) {
//		�ҵ�Ŀǰ����id��������1��Ϊ��Ƶ����id
//		int newId=channels.get(channels.size()-1).getId()+1;
//		c.setId(newId);
//		channels.add(c);
//		return c;
		return repo.save(c);
		}
	
	/**
	 * ����ָ����Ƶ����Ϣ
	 * @param c �µ�Ƶ����Ϣ�����ڸ����Ѵ��ڵ�ͬһƵ��
	 * @return ���º��Ƶ����Ϣ
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
		//��������׷�ӵ������ۺ���
		if (c.getComments() !=null) {
			if(saved.getComments() != null) {
			saved.getComments().addAll(c.getComments());
			}else {//�������۴���������
				saved.setComments(c.getComments());
			}
		}
		if (c.getCover() !=null) {
			saved.setCover(c.getCover());
		}
		
		return repo.save(saved);//������º��ʵ�����
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
	 * ��ȡ����Ƶ��
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
	 * ��ָ��Ƶ�����һ������
	 * @param channelId Ŀ��Ƶ���ı��
	 * @param comment ������ӵ�Ƶ��
	 */
	public Channel addComment(String channelId, Comment comment) {
		Channel result = null;
		Channel saved = getChannel(channelId);
		if (null != saved) {//���ݿ����и�Ƶ��
			saved.addComment(comment);
			result = repo.save(saved);
		}
		return result;
	}
	/**
	 * ��ȡĿ��Ƶ������������
	 * @param channelId Ŀ��Ƶ���ı��
	 * @return �����б�
	 */
	public List<Comment>hotComments(String channelId){
		List<Comment> result = null;
		Channel saved = getChannel(channelId);
		if (saved != null) {
		result = saved.getComments();
		result.sort(new Comparator<Comment>() {
			@Override
			public int compare(Comment o1,Comment o2) {
		     //��o1��o2С���򷵻ظ�������o1��o2���򷵻���������o1����o2���򻹻�0
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
		logger.debug("����������"+result.size()+"��...");
		logger.debug(result.toString());
		}else {
			logger.warn("ָ����Ƶ�������ڣ�id="+channelId);
		}
		return result;
	}
}
	
	


	
	


