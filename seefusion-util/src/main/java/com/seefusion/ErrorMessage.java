package com.seefusion;

public class ErrorMessage extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2093794453674631099L;
	private int code = 500;

	public ErrorMessage(String message) {
		super(message);
	}

	public ErrorMessage(Throwable t) {
		super(t);
	}

	public ErrorMessage(String message, Throwable t) {
		super(message, t);
	}

	public ErrorMessage(int code, String message) {
		super(message);
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
}