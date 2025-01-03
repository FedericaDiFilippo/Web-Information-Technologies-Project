package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Comment;


public class CommentDAO {
	
	private Connection connection;

	public CommentDAO(Connection connection) {
		this.connection = connection;
	}
	
	
	public void createComment(int imageID, String creatorUsername, String text) throws SQLException {
		String query = "INSERT INTO comment ( imageID, creatorUsername, text) VALUES ( ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, imageID);
			pstatement.setString(2, creatorUsername);
			pstatement.setString(3, text);
			pstatement.executeUpdate();
        }
	}
	
	
	public List<Comment> findAllCommentsById(int imageID) throws SQLException{
		List<Comment> allCommentsForImage= new ArrayList<Comment>();
		String query = "SELECT * FROM comment WHERE imageID = ? ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, imageID);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Comment comment = new Comment();
					comment.setId(result.getInt("id"));
					comment.setImageID(result.getInt("imageID"));
					comment.setCreatorUsername(result.getString("creatorUsername"));
					comment.setText(result.getString("text"));
					allCommentsForImage.add(comment);
				}
			}
		}
		return allCommentsForImage;
	}
}
