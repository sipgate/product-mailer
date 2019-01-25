package com.sipgate.product.mail;

import static com.sipgate.product.mail.Postman.prepareMail;
import static java.util.Arrays.stream;
import static javax.mail.Message.RecipientType.TO;
import static org.slf4j.LoggerFactory.getLogger;

import com.sipgate.product.mail.Postman.Mail;
import com.sipgate.product.mail.exception.SmtpStartupException;
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

public class Smtp {

	private static final Logger LOGGER = getLogger(Smtp.class);

	private static SMTPServer server = null;

	public static boolean startupSmtpService() throws SmtpStartupException {

		if (server == null) {

			final BasicMessageListener handler = (context, from, to, data) -> {
				final Mail mail = prepareMail(buildMail(data)).send();
				LOGGER.info("Sent mail to {}", mail.getTo());
			};

			server = SMTPServer.port(10025)
					.messageHandler(handler)
					.build();

			server.start();

			return true;
		}

		throw new SmtpStartupException();
	}

	private static Mail buildMail(final byte[] data) {
		try {
			Session s = Session.getInstance(new Properties());
			InputStream is = new ByteArrayInputStream(data);
			MimeMessage message = new MimeMessage(s, is);

			return Postman.newMail()
					.withMessage(message.getContent().toString())
					.withSubject(message.getSubject())
					.from(stream(message.getFrom())
							.map(address -> address.toString())
							.collect(Collectors.joining(",")))
					.to(stream(message.getRecipients(TO))
							.map(address -> address.toString())
							.map(address -> Database.getInstance().getCustomerEmailAddressFor(address))
							.flatMap(address -> address.isPresent() ? Stream.of(address.get()) : Stream.empty())
							.collect(Collectors.joining(",")));
		} catch (MessagingException | IOException e) {
			throw new RuntimeException("Failed to parse incoming email", e);
		}
	}
}
