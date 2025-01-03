package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polimi.tiw.projects.beans.Album;

public class AlbumDAO {
	private Connection connection;

	public AlbumDAO(Connection connection) {
		this.connection = connection;
	}

	public void createAlbum(String title, String creator, Date creationDate) throws SQLException {
		String query = "INSERT INTO album (title, creator, creationDate)   VALUES(?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, title);
			pstatement.setString(2, creator);
			pstatement.setObject(3, creationDate.toInstant().atZone(ZoneId.of("Europe/Rome")).toLocalDate());
			pstatement.executeUpdate();
		}
	}

	public Album findAlbumById(int albumID) throws SQLException {
		String query = "SELECT * FROM album WHERE id = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, albumID);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next())
					return this.dbToAlbum(result);
				return null;
			}
		}
	}
	
	//usato per vedere se sono già presenti album con lo stesso titolo
	public Album findAlbumByTitle(String title, String creator) throws SQLException {
		String query = "SELECT * FROM album WHERE title = ? AND creator = ? ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, title);
			pstatement.setString(2, creator);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next())
					return this.dbToAlbum(result);
				return null;
			}
		}
	}

	
	public List<Album> findAllAlbums() throws SQLException {
		List<Album> allAlbum = new ArrayList<Album>();
		String query = "SELECT id, title, creationDate, creator FROM album  ORDER BY creationDate DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Album album = dbToAlbum(result);
					allAlbum.add(album);
				}
			}
		}
		return allAlbum;
	}
	
	//usato solo se l'user aveva già salvato un ordine, in quel caso gli album dell'user
	//vengono trovati con findAlbumById, mentre quelli degli altri con questa
	public List<Album> findOthersAlbums(String username) throws SQLException {
		List<Album> allAlbum = new ArrayList<Album>();
		String query = "SELECT id, title, creationDate, creator FROM album WHERE creator <>?  ORDER BY creationDate DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Album album = dbToAlbum(result);
					allAlbum.add(album);
				}
			}
		}
		return allAlbum;
	}

	//per trovare quante pagine ci sono nell'album
	public Album dbToAlbum(ResultSet result) throws SQLException {
		Album album = new Album();
		album.setId(result.getInt("id"));
		album.setTitle(result.getString("title"));
		album.setCreationDate(result.getDate("creationDate"));
		album.setCreator(result.getString("creator"));
		return album;
	}
		
		
}

