package com.appkilt.client;

abstract class RestException extends Exception {

	private static final long serialVersionUID = 7245646124031272398L;

	public RestException(String message)
	{
		super(message);
	}
}
