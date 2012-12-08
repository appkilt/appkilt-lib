package com.appkilt.client;

class RestConnectionFailedException extends RestException {

	private static final long serialVersionUID = -8210412938428995183L;

	public RestConnectionFailedException(String message) {
		super(message);
	}

}
