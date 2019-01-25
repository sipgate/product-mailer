package com.sipgate.product.mail.exception;

public class ProductMailerException extends Exception {

	public ProductMailerException() {
	}

	public ProductMailerException(final String message) {
		super(message);
	}

	public ProductMailerException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ProductMailerException(final Throwable cause) {
		super(cause);
	}

	public ProductMailerException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
