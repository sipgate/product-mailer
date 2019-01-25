package com.sipgate.product.mail;

import static com.sipgate.product.mail.Postman.prepareMail;
import static java.util.Arrays.stream;
import static javax.mail.Message.RecipientType.TO;
import static org.slf4j.LoggerFactory.getLogger;

import com.sipgate.product.mail.Postman.Mail;
import com.sipgate.product.mail.exception.ProductMailerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.subethamail.smtp.helper.BasicMessageListener;
import org.subethamail.smtp.server.SMTPServer;

public class Main {

	private static final Logger LOGGER = getLogger(Main.class);

	public static void main(String[] args) {
		new Main().startupService();
	}

	private void startupService() {

		try {
			LOGGER.info("Starting service...");

			Database.checkConnection();

			LOGGER.info("Database connection established successfully.");

			Smtp.startupSmtpService();

			LOGGER.info("SMTP server started");

		} catch (ProductMailerException e) {

			LOGGER.error("Failed to startup service: {}", e.getMessage());

		}
	}


}
