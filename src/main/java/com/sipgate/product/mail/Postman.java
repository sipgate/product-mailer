package com.sipgate.product.mail;


import static org.slf4j.LoggerFactory.getLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;

public class Postman {

	private static final Logger LOGGER = getLogger(Postman.class);

	public static Mail newMail() {
		return new Mail();
	}

	public static class Mail {

		private String subject;
		private String message;
		private String from;
		private String to;

		private Mail() {
			super();
		}

		public Mail withSubject(String subject) {
			this.subject = subject;
			return this;
		}

		public Mail withMessage(String message) {
			this.message = message;
			return this;
		}

		public Mail from(String from) {
			this.from = from;
			return this;
		}

		public Mail to(String to) {
			this.to = to;
			return this;
		}

		public String getSubject() {
			return subject;
		}

		public String getMessage() {
			return message;
		}

		public String getFrom() {
			return from;
		}

		public String getTo() {
			return to;
		}
	}

	public static SendMailOperation prepareMail(Mail mail) {
		return new SendMailOperation(mail);
	}

	public static class SendMailOperation {

		private Postman.Mail mail;

		private SendMailOperation(Mail mail) {
			this.mail = mail;
		}

		public Mail send() {

			Properties props = new Properties();

			try (InputStream input = new FileInputStream("/usr/local/etc/product-mailer.properties")) {
				props.load(input);
			} catch (IOException e) {
				LOGGER.error("Cannot load configuration", e);
			}

			final String password = props.getProperty("smtp.password");
			final String username = props.getProperty("smtp.username");

			final Session sessionobj = Session.getInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username, password);
						}
					});

			try {
				Message messageobj = new MimeMessage(sessionobj);
				messageobj.setFrom(new InternetAddress(mail.from));
				messageobj.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.to));
				messageobj.setSubject(mail.subject);
				messageobj.setText(mail.message);

				Transport transport = sessionobj.getTransport("smtp");
				String mfrom = "relay.musil";
				transport.connect("smtp.gmail.com", mfrom, password);
				transport.sendMessage(messageobj, messageobj.getAllRecipients());

			} catch (MessagingException exp) {
				throw new RuntimeException(exp);
			}

			return mail;
		}
	}
}
