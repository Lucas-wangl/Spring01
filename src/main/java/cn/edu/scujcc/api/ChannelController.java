package cn.edu.scujcc.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.scujcc.model.Channel;
import cn.edu.scujcc.model.Comment;
import cn.edu.scujcc.model.Result;
import cn.edu.scujcc.service.ChannelService;
import cn.edu.scujcc.service.UserService;

@RestController
@RequestMapping("/channel")

public class ChannelController {
	public static final Logger logger = LoggerFactory.getLogger(ChannelController.class);
		
	@Autowired
	private ChannelService service;
	@Autowired
	private UserService userService;
	
	@GetMapping
	public Result<List<Channel>> getAllChannels(){
		logger.info("���ڲ�������Ƶ����Ϣ������");
		Result<List<Channel>> result = new Result<List<Channel>>();
		List<Channel> channels = service.getAllChannels();
		result = result.ok();
		result.setData(channels);
		return result;
	}
	@GetMapping("/{id}")
	public Result<Channel> getChannel(@PathVariable String id) {
		logger.info("���ڶ�ȡƵ����"+id);
		Result<Channel> result = new Result<>();
		Channel c =service.getChannel(id);
		if (c !=null) {
			result = result.ok();
			result.setData(c);
		}else {
			logger.error("�Ҳ���ָ����Ƶ����");
			result = result.error();
			result.setMessage("�Ҳ���ָ����Ƶ����");
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
			result.setStatus(Result.ERROR);
			result.setMessage("ɾ��ʧ��");
		}
		return result;
	}
	@PostMapping
	public Result<Channel> createChannel(@RequestBody Channel c) {
		logger.info("�����½�Ƶ����Ƶ�����ݣ�"+c);
		Result<Channel> result = new Result<>();
		Channel saved= service.createChannel(c);
		result = result.ok();
		result.setData(saved);
		return result;
	}
	@PutMapping
	public Result<Channel> updateChannel(@RequestBody Channel c) {
		logger.debug("��������Ƶ����Ƶ�����ݣ�" + c);
		Result<Channel> result = new Result<>();
		Channel updated = service.updateChannel(c);
		result = result.ok();
		result.setData(updated);
		return result;
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
	public Channel addComment(@RequestHeader("token") String token,@PathVariable String channelId,@RequestBody Comment comment) {
		Channel result = null;
		logger.debug("��������Ƶ��:"+channelId+",���۶���:"+comment);
		//�����۱��������ݿ�
		String username = userService.currentUser(token);
		logger.debug("��¼�û�"+username+"��������...");
		comment.setAuthor(username);
		result = service.addComment(channelId, comment);
		return result;
	}
	@GetMapping("/{channelId}/hotcomments")
	public Result<List<Comment>> hotComments(@PathVariable String channelId){
		Result<List<Comment>> result = new Result<List<Comment>>();
		logger.debug("��ȡƵ��"+channelId+"����������.....");
		result = result.ok();
		result.setData(service.hotComments(channelId));
		return result;
	}
}
