package cn.edu.scujcc.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.scujcc.model.Channel;
import cn.edu.scujcc.model.Comment;
import cn.edu.scujcc.model.Result;
import cn.edu.scujcc.model.User;
import cn.edu.scujcc.service.ChannelService;

@RestController
@RequestMapping("/channel")

public class ChannelController {
	public static final Logger logger = LoggerFactory.getLogger(ChannelController.class);
	
	@Autowired
	private CacheManager cacheManager;
	
	@Autowired
	private ChannelService service;
	@GetMapping
	public Result<List<Channel>> getAllChannels(){
		logger.info("���ڲ�������Ƶ����Ϣ������");
		Result<List<Channel>> result = new Result<List<Channel>>();
		List<Channel> channels = service.getAllChannels();
		result = result.ok();
		result.setDate(channels);
		return result;
	}
	@GetMapping("/{id}")
	public Result<Channel> getChannel(@PathVariable String id) {
		logger.info("���ڶ�ȡƵ����"+id);
		Result<Channel> result = new Result<>();
		Channel c =service.getChannel(id);
		if (c !=null) {
			result = result.ok();
			result.setDate(c);
		}else {
			logger.error("�Ҳ���ָ����Ƶ����");
			result = result.error();
		}
		return result;
	}
	@DeleteMapping("/{id}")
	public Result<Channel> deleteChannel(@PathVariable String id){
		logger.info("����ɾ��Ƶ����id="+id);
		Result<Channel> result = new Result<Channel>();
		boolean del = service.deteleChannel(id);
		if(del) {
			result = result.ok();
		}else {
			result = result.error();
		}
		return result;
	}
	@PostMapping
	public Channel createChannel(@RequestBody Channel c) {
		logger.info("�����½�Ƶ����Ƶ�����ݣ�"+c);
		Channel saved=service.createChannel(c);
		return saved;
	}
	@PutMapping
	public Channel updateChannel(@RequestBody Channel c) {
		System.out.println("��������Ƶ����Ƶ�����ݣ�"+c);
		Channel update=service.updateChannel(c);
		return update;
	
}
	@GetMapping("/q/{quality}")
	public List<Channel>searchByQuality(@PathVariable String quality){
		return service.searchByQuality(quality);
	}
	@GetMapping("/t/{title}")
	public List<Channel>searchByTitle(@PathVariable String title){
		return service.searchByTitle(title);
	}
	@GetMapping("/cold")
	public List<Channel> getColdChannels(){
		return service.findColdChannels();
	}
	@GetMapping("/p/{page}")
	public List<Channel> getChannelsPage(@PathVariable int page){
		return service.findChannelsPage(page);
	}
	@PostMapping("/{channelId}/comment")
	public Channel addComment(@PathVariable String channelId,@RequestBody Comment comment) {
		Channel result = null;
		logger.debug("��������Ƶ��:"+channelId+",���۶���:"+comment);
		//���ȼ���û��Ƿ��¼��
		Cache cache = cacheManager.getCache(User.CACHE_NAME);
		ValueWrapper obj = cache.get("current_user");
		if (obj == null) {
			logger.warn("�û�δ��¼���������ۣ�");
		}else {
		//�����۱��������ݿ�
			String username = (String) obj.get();
			logger.debug("��¼�û�"+username+"��������...");
			comment.setAuthor(username);
		result = service.addComment(channelId,comment);
		}
		return result;
	}
	@GetMapping("/{channelId}/hotcomments")
	public Result<List<Comment>> hotComments(@PathVariable String channelId){
		Result<List<Comment>> result = new Result<List<Comment>>();
		logger.debug("��ȡƵ��"+channelId+"����������.....");
		result.setStatus(Result.OK);
		result.setMessage("�����ɹ�");
		result.setDate(service.hotComments(channelId));
		return result;
	}
}
