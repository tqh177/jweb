package cn.tqhweb.jweb.plugin.session;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import cn.tqhweb.jweb.http.Request;
import cn.tqhweb.jweb.http.Response;
import cn.tqhweb.jweb.plugin.cache.EhCachePlugin;
import cn.tqhweb.jweb.util.StrKit;
import cn.tqhweb.jweb.util.log.Logger;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@SuppressWarnings("deprecation")
public class EhCacheSessionPlugin extends SessionPlugin {
	private static Logger logger = Logger.factory(EhCacheSessionPlugin.class);
	private Ehcache cache;
	private EhCachePlugin plugin;
	private int number = 0;
	private String name = "JSESSIONID";

	public void setSessionName(String name) {
		this.name = name;
	}

	@Override
	public synchronized boolean start() {
		number++;
		if (cache == null) {
			// 单例缓存插件
			plugin = (EhCachePlugin) EhCachePlugin.getCachePlugin();
			if (plugin == null) {
				plugin = EhCachePlugin.getInstance();
			}
			if (!plugin.start()) {
				return false;
			}
			cache = plugin.getCacheManager().getCache("session");
			if (cache == null) {
				return false;
			}
			register(this);
		}
		return true;
	}

	@Override
	public synchronized boolean stop() {
		number--;
		if (number == 0) {
			System.out.println("刷新缓存:session");
			cache.flush();
			cache = null;
		}
		return plugin.stop();
	}

	@Override
	public int getTotal() {
		return cache.getSize();
	}

	@Override
	protected HttpSession getSession_(Request request, Response response, boolean create) {
		String id = request.getRequestedSessionId();
		Session session;
		if (!create) {
			Element element;
			if (id == null || (element = cache.get(id)) == null) {
				logger.debug("没有session");
				return null;
			}
			session = new Session(element);
		} else {
			logger.debug("创建新的session");
			session = new Session((String) null);
		}
		session.init(request, response);
		return session;
	}

	@Override
	protected String getSessionName_() {
		return name;
	}

	public class Session implements HttpSession {
		private String id;
		private Map<String, Object> map;
		private Request request;
		private Response response;
		private Element element;
		private boolean isNew;

		void init(Request request, Response response) {
			this.request = request;
			this.response = response;
			// 向Response添加session的Cookie
			if (isNew) {
				Cookie cookie = new Cookie(name, id);
				cookie.setHttpOnly(true);
				response.addCookie(cookie);
			}
		}

		private String createId(String id, int index) {
			if (index > 10) {
				throw new IllegalArgumentException("session生成次数超限");
			}
			char[] cs = new char[32];
			Random random = new Random();
			for (int i = 0; i < cs.length; i++) {
				cs[i] = (char) (random.nextInt(Character.MAX_VALUE) ^ index ^ random.hashCode());
			}
			if (id == null) {
				return StrKit.md5(String.valueOf(cs));
			}
			id = (index | 1) == 0 ? (id + String.valueOf(cs)) : (id + index + String.valueOf(cs));
			return StrKit.md5(id);
		}

		@SuppressWarnings("unchecked")
		public Session(String id) {
			if (id == null || (element = cache.get(id)) == null) {
				int index = 0;
				while ((element = cache.get(id = createId(id, index))) != null) {
					index++;
				}
				isNew = true;
				map = new ConcurrentHashMap<>();
				element = new Element(id, (Serializable) map);
				cache.put(element);
			} else {
				isNew = false;
				map = (Map<String, Object>) element.getObjectValue();
			}
			this.id = id;
		}

		@SuppressWarnings("unchecked")
		public Session(Element element) {
			this.element = element;
			this.map = (Map<String, Object>) element.getObjectValue();
			this.id = (String) element.getObjectKey();
			this.isNew = false;
		}

		@Override
		public long getCreationTime() {
			return element.getCreationTime();
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public long getLastAccessedTime() {
			return element.getLastAccessTime();
		}

		@Override
		public ServletContext getServletContext() {
			return request.getServletContext();
		}

		@Override
		public void setMaxInactiveInterval(int interval) {
			element.setTimeToIdle(interval);
		}

		@Override
		public int getMaxInactiveInterval() {
			return element.getTimeToIdle();
		}

		@Override
		@Deprecated
		public HttpSessionContext getSessionContext() {
			return null;
		}

		@Override
		public Object getAttribute(String name) {
			return map.get(name);
		}

		@Override
		@Deprecated
		public Object getValue(String name) {
			return getAttribute(name);
		}

		@Override
		public Enumeration<String> getAttributeNames() {
			return Collections.enumeration(map.keySet());
		}

		@Override
		@Deprecated
		public String[] getValueNames() {
			return map.keySet().toArray(new String[0]);
		}

		@Override
		public void setAttribute(String name, Object value) {
			map.put(name, value);
		}

		@Override
		@Deprecated
		public void putValue(String name, Object value) {
			setAttribute(name, value);
		}

		@Override
		public void removeAttribute(String name) {
			map.remove(name);
		}

		@Override
		@Deprecated
		public void removeValue(String name) {
			removeAttribute(name);
		}

		@Override
		public void invalidate() {
			logger.debug("session销毁");
			cache.remove(element);
			element = null;
			id = null;
			Cookie cookie = new Cookie(name, "deleted");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}

		@Override
		public boolean isNew() {
			return isNew;
		}
	}
}