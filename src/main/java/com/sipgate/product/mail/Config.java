package com.sipgate.product.mail;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;

public class Config {

	private static final Logger LOGGER = getLogger(Config.class);
	private static final Config instance = new Config();

	public static Config getConfig() {
		return instance;
	}

	private Properties props = new Properties();

	private Config() {
		try (InputStream input = new FileInputStream("/usr/local/etc/product-mailer.properties")) {
			props.load(input);
		} catch (IOException e) {
			LOGGER.error("Cannot load configuration", e);
		}
	}

	public Properties getProps() {
		return props;
	}

	public String getDbName() {
		return props.get("db.name").toString();
	}


	public String getDbInstanceName() {
		return props.get("db.instancename").toString();
	}

	public String getDbUsername() {
		return props.get("db.username").toString();
	}

	public String getDbPassword() {
		return props.get("db.password").toString();
	}
}
