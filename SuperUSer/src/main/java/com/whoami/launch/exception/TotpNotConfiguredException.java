package com.whoami.launch.exception;

public class TotpNotConfiguredException extends RuntimeException {

	public TotpNotConfiguredException(String message) {
		super(message);
	}
}