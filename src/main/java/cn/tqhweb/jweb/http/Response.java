package cn.tqhweb.jweb.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import cn.tqhweb.jweb.App;

public interface Response extends HttpServletResponse {
	static final String DEFAULT_CONTENT_TYPE = "text/plain";
	static final String HTML_CONTENT_TYPE = "text/html";
	static final String JSON_CONTENT_TYPE = "application/json";
	static final String JSONP_CONTENT_TYPE = "application/javascript";

	public static Response wrap(HttpServletResponse res, App app) {
		return new ResponseWrapper(res, app);
	}
	public void sendRedirect(String url);
	public void sendRedirect(String url, int stateCode);
	public void sendJSON(Object jsonObject);
	public void sendText(String string);
	public void sendChars(char[] cs);
	public String getContent();
	public void send() throws ServletException, IOException;
	public void close() throws IOException;
}
