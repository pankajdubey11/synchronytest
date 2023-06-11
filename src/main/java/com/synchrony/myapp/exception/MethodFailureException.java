package com.synchrony.myapp.exception;

public class MethodFailureException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public MethodFailureException(String message) {
		super(message);
	}

}
