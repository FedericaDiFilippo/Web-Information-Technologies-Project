package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.projects.beans.User;

public class UserDAO {

	private Connection connection;

	public UserDAO(Connection connection) {
		this.connection = connection;
	}

	public void signUp(String username, String email, String password) throws SQLException {
		String query = "INSERT INTO user (username, email, password) VALUES (?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setString(1, username);
			pstatement.setString(2, email);
			pstatement.setString(3, password);
			pstatement.executeUpdate();

		}

	}

	public User login(String username, String password) throws SQLException {
		String query = "SELECT username, email FROM user WHERE username = ? AND password = ? ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setEmail(result.getString("email"));
					user.setUsername(result.getString("username"));
					return user;
				}
			}
		}
	}

	public User findUserByName(String username) throws SQLException {
		String query = "SELECT * FROM user WHERE username = ?";
		try (PreparedStatement pStatement = connection.prepareStatement(query);) {
			pStatement.setString(1, username);
			try (ResultSet result = pStatement.executeQuery();) {
				if (result.next())
					return this.dbToUser(result);
				return null;
			}
		}
	}

	public User dbToUser(ResultSet result) throws SQLException {
		User user = new User();
		user.setEmail(result.getString("email"));
		user.setUsername(result.getString("username"));
		user.setPassword(result.getString("password"));
		return user;
	}

	public void updateAlbumsOrder(String ordered, String username) throws SQLException {
		String query = "UPDATE user SET albumsOrder = ? WHERE username = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setString(1, ordered);
			pstatement.setString(2, username);
			pstatement.executeUpdate();
		}
	}
	
	public String getOrderedAlbums(String username) throws SQLException {
		String query = "SELECT albumsOrder FROM user WHERE username = ?";
		try (PreparedStatement pStatement = connection.prepareStatement(query);) {
			pStatement.setString(1, username);
			try (ResultSet result = pStatement.executeQuery();) {
				if (result.next()) 
					return result.getString("albumsOrder");
				else return null;
			}
		}
	}

}
