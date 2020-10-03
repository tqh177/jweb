package cn.tqhweb.jweb.http;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import cn.tqhweb.jweb.App;
import cn.tqhweb.jweb.util.json.JSON;

class ResponseWrapper extends HttpServletResponseWrapper implements Response {

	// 缓冲区
	private CharArrayWriter buff;
	private PrintWriter writer;

	@Override
	public PrintWriter getWriter() {
		return writer;
	}

	@Override
	public void flushBuffer() throws IOException {
		super.getWriter().write(buff.toCharArray());
		super.flushBuffer();
		buff.reset();
	}

	@Override
	public void close() throws IOException {
		flushBuffer();
		buff.close();
		writer.close();
		super.getWriter().close();
		buff = null;
		writer = null;
	}
	
	@Override
	public String getContent() {
		return String.valueOf(buff);
	}

	@SuppressWarnings("unused")
	private App app;
	ResponseWrapper(HttpServletResponse response, App app) {
		super(response);
		this.app = app;
		buff = new CharArrayWriter();
		writer = new PrintWriter(buff);
	}
	
	@Override
	public void addCookie(Cookie cookie) {
		if (cookie.getPath() == null) {
			cookie.setPath("/");
		}
		super.addCookie(cookie);
	}

	/**
	 * 重定向
	 * 
	 * @param uri
	 * @return
	 */
	@Override
	public void sendRedirect(String url) {
		sendRedirect(url, 301);
	}

	@Override
	public void sendRedirect(String url, int stateCode) {
		setStatus(stateCode);
		setHeader("Location", url);
	}

	@Override
	public void sendJSON(Object jsonObject) {
		setContentType(JSON_CONTENT_TYPE);
		String text = JSON.stringify(jsonObject);
		if (text == null) {
			text = "{}";
		}
		getWriter().write(text);
	}

	@Override
	public void sendText(String text) {
		if(getContentType() == null)
			setContentType(HTML_CONTENT_TYPE);
		getWriter().write(text);
	}
	
	@Override
	public void sendChars(char[] cs) {
		setContentType(HTML_CONTENT_TYPE);
		getWriter().write(cs);
	}
	
	@Override
	public void send() throws ServletException, IOException {
		close();
	}
}
