package com.appkilt.client;

class RestUnauthorizedException extends RestException {

	private static final long serialVersionUID = -6286422499520891779L;

	public RestUnauthorizedException(String message) {
		super(message);
	}

}
