package cn.tqhweb.jweb.exception;

public class HttpException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private int stateCode;

	public HttpException(int stateCode, String message) {
		super(message);
		init(stateCode);
	}

	public HttpException(int stateCode) {
		super(getHttpStateMessage(stateCode));
		init(stateCode);
	}

	public int getStateCode() {
		return stateCode;
	}

	private void init(int stateCode) {
		this.stateCode = stateCode;
//		App.getApp().getResponse().setStatus(stateCode);
//		try {
//			App.getApp().getResponse().sendError(stateCode);
//		} catch (IOException e) {
//			Thread.interrupted();
//		}
	}

	/**
	 * 状态码转message
	 * 
	 * @param stateCode
	 * @return
	 */
	private static String getHttpStateMessage(int stateCode) {
		String msg = null;
		switch (stateCode) {
		case 404:
			msg = "404 NOT FOUND!";
			break;
		case 500:
			msg = "the server is down";
		default:
			msg = "unknow error";
			break;
		}
		return msg;
	}

	/**
	 * 异常构造函数会调用 fillInStackTrace() 构建整个调用栈，消耗较大 而 ActionException
	 * 无需使用调用栈信息，覆盖此方法用于提升性能
	 */
	@Override
	public Throwable fillInStackTrace() {
		return this;
	}
}
