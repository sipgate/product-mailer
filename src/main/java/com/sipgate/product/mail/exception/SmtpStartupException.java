package com.sipgate.product.mail.exception;

public class SmtpStartupException extends ProductMailerException {
	public SmtpStartupException() {
		super("Failed to startup the SMTP service");
	}
}
