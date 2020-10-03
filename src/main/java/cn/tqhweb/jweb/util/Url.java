package cn.tqhweb.jweb.util;

import java.net.MalformedURLException;
import java.net.URL;

public class Url {
	private String host;
	public Url(String url) {
		
	}

	static public URL parse(String url) throws MalformedURLException {
		return new URL(url);
	}
	public String getHost() {
		return host;
	}
}
