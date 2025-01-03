package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polimi.tiw.projects.beans.Image;

public class ImageDAO {

	private Connection connection;

	public ImageDAO(Connection connection) {
		this.connection = connection;
	}

	public void createImage(int idAlbum, String title, Date date, String descriptionText, String path,
			String creatorUsername) throws SQLException {
		String query = "INSERT INTO image (idAlbum, title, date, descriptionText, path, creatorUsername) VALUES (?, ?, ?, ?, ?,?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idAlbum);
			pstatement.setString(2, title);
			pstatement.setObject(3, date.toInstant().atZone(ZoneId.of("Europe/Rome")).toLocalDate());
			pstatement.setString(4, descriptionText);
			pstatement.setString(5, path);
			pstatement.setString(6, creatorUsername);
			pstatement.executeUpdate();
		}
	}

	public Image findImageById(int id) throws SQLException {
		String query = "SELECT * FROM image WHERE id = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, id);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next())
					return this.dbToImage(result);
				return null;
			}
		}
	}

	//in ADD IMAGE per vedere se nell'album ci sono già immagini con lo stesso titolo
	public Image findImageByTitle(String title, int idAlbum) throws SQLException {
		String query = "SELECT * FROM image WHERE title = ? AND idAlbum = ? ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, title);
			pstatement.setInt(2, idAlbum);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next())
					return this.dbToImage(result);
				return null;
			}
		}
	}
	
	//in ADD IMAGE per vedere se l'immagine è presente nel database
	public Image findImageByPath(String path) throws SQLException {
		String query = "SELECT * FROM image WHERE path = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, path);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next())
					return this.dbToImage(result);
				return null;
			}
		}
	}

	public Image dbToImage(ResultSet result) throws SQLException {
		Image image = new Image();
		image.setId(result.getInt("id"));
		image.setIdAlbum(result.getInt("idAlbum"));
		image.setTitle(result.getString("title"));
		image.setDate(result.getDate("date"));
		image.setDescriptionText(result.getString("descriptionText"));
		image.setPath(result.getString("path"));
		image.setCreatorUsername(result.getString("creatorUsername"));
		return image;
	}

	public List<Image> findAllImagesByAlbumId(int idAlbum) throws SQLException {
		List<Image> allImagesInAlbum = new ArrayList<Image>();
		String query = "SELECT id, idAlbum,title,date,descriptionText,path, creatorUsername FROM image WHERE idAlbum = ? ORDER BY Date DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idAlbum);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Image image = dbToImage(result);
					allImagesInAlbum.add(image);
				}
			}
		}
		return allImagesInAlbum;
	}
}
