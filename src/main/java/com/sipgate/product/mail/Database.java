package com.sipgate.product.mail;

import static com.sipgate.product.mail.Config.getConfig;
import static org.slf4j.LoggerFactory.getLogger;

import com.sipgate.product.mail.exception.DatabaseConnectionException;
import com.sipgate.product.mail.exception.ProductMailerException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.slf4j.Logger;

public class Database {

	private static final Logger LOGGER = getLogger(Database.class);

	private Connection connection;
	private static Database instance;

	public static Database getInstance() {
		if (instance == null) {
			instance = new Database();
		}
		return instance;
	}

	private Database() {
		super();
	}

	private Connection getConnection() {
		if (this.connection == null) {
			final String jdbcUrl = String.format(
					"jdbc:mysql://google/%s?cloudSqlInstance=%s&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
					getConfig().getDbName(),
					getConfig().getDbInstanceName());
			try {
				this.connection = DriverManager.getConnection(jdbcUrl, getConfig().getDbUsername(), getConfig().getDbPassword());
			} catch (SQLException e) {
				throw new RuntimeException("Failed to get SQL connection", e);
			}
		}

		return this.connection;
	}

	public static boolean checkConnection() throws ProductMailerException {
		try {

			if (getInstance().getConnection() == null) {
				throw new DatabaseConnectionException();
			}

			return true;
		} catch (Exception e) {
			throw new DatabaseConnectionException();
		}
	}

	public synchronized Optional<String> getCustomerEmailAddressFor(String productMailAddress) {

		final String user = productMailAddress.split("@")[0];
		final String query = "select email from user where id = ?";

		try (final PreparedStatement statement = getConnection().prepareStatement(query)) {

			statement.setString(1, user);
			statement.execute();

			final ResultSet resultSet = statement.getResultSet();
			if (resultSet.next()) {
				final String result = resultSet.getString("email");

				return Optional.ofNullable(result);
			} else {
				LOGGER.warn("Found no address for {}", productMailAddress);
				return Optional.empty();
			}
		} catch (SQLException e) {
			LOGGER.warn("Failed to fetch user email address for {}", productMailAddress, e);
			return Optional.empty();
		}
	}


}

