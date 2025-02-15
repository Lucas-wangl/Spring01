package cn.edu.scujcc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


import cn.edu.scujcc.model.User;

/**
 * 认证拦截器，用于检查用户是否登录
 * @author dell
 *
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

	@Autowired
	private CacheManager cacheManager;
	private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
	/**
	 * 用户如果已经登录，则还回true，否则还回false
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean logged = false;
		String target = request.getRequestURI();
		//把登陆与注册排除在保护之外
		if(null != target && target.startsWith("/user")) {
			return true;
		}
		//把出错界面也排除在保护之外
		if (response.getStatus() == HttpServletResponse.SC_FORBIDDEN) {
			return true;
		}
		String token = request.getHeader("token");
		if(token != null) {
			Cache cache = cacheManager.getCache(User.CACHE_NAME);
			String username = cache.get(token,String.class);
			if(username != null) {
				logged = true;
				logger.debug("用户"+username+"允许访问"+request.getRequestURI());
			}
		}
		if(!logged) {
			logger.warn("非法访问"+request.getRequestURI());
			response.sendError(HttpServletResponse.SC_FORBIDDEN,"未登录用户禁止访问");
		}
		return logged;
		
	}

}
