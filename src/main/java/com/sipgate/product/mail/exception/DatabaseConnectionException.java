package com.sipgate.product.mail.exception;

public class DatabaseConnectionException extends ProductMailerException {

	public DatabaseConnectionException() {
		super("Failed to establish database connection.");
	}

	public DatabaseConnectionException(final Throwable cause) {
		super("Failed to establish database connection.", cause);
	}
}
