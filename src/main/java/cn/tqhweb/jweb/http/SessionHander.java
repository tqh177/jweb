package cn.tqhweb.jweb.http;

import javax.servlet.http.HttpSession;

public class SessionHander {
	private static ISessionHander sessionHander;

	public static interface ISessionHander {
		public static HttpSession getSession(Request request, Response response) {
			if (sessionHander == null) {
				return null;
			}
			return sessionHander.getHttpSession(request, response);
		}

		public static void register(ISessionHander sessionHander) {
			SessionHander.sessionHander = sessionHander;
		}

		public abstract HttpSession getHttpSession(Request request, Response response);
	}
}
